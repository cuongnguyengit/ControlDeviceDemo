from __init__ import create_app, db
from flask import jsonify, flash, request, make_response
from werkzeug.security import generate_password_hash, check_password_hash
from model import User, DeviceDemo
from flask_login import login_user, logout_user, login_required
from flask_cors import CORS
from test import recognition_with_wav
from unidecode import unidecode
import re

app = create_app()
db.create_all(app=app)
CORS(app)

@app.route('/get-all-devices', methods = ['GET'])
def get():
    response = {}
    result = {}
    tmp = DeviceDemo.query.filter_by(is_active=1).all()
    for i, t in enumerate(tmp):
        if t.status:
            stt = 1
        else:
            stt = 0
        result[str(i)] = t.name + "," + str(stt)
    response[str(len(tmp))] = result
    res = make_response(jsonify(result))
    res.headers['Access-Control-Allow-Origin'] = '*'
    # return ''
    return res

@app.route('/update-status-device', methods = ['POST'])
def update_status_device():
    msg_received = request.get_json()
    name = msg_received["name"]
    stt = msg_received["status"]
    d = DeviceDemo.query.filter_by(name=name).first()
    if not d:
        return "failure"
    d.command = -1
    d.status = str(stt) == str("1")
    db.session.commit()
    return "success"

@app.route('/get-command', methods = ['POST'])
def get_command_device():
    msg_received = request.get_json()
    name = msg_received["name"]
    d = DeviceDemo.query.filter_by(name=name).first()
    if not d:
        return "-1"
    return str(d.command)

@app.route('/update-device', methods = ['POST'])
def update():
    msg_received = request.get_json()
    print(msg_received)
    username = msg_received["username"]
    password = msg_received["password"]
    user = User.query.filter_by(name=username).first()
    if not user or not check_password_hash(user.pwd, password):
        return "failure"
    command = msg_received["command"]
    command, name = hand_cmd(command)
    if unidecode(command) == "on" or unidecode(command) == "bat":
        command = 1
    else:
        command = 0
    d = DeviceDemo.query.filter_by(name=name).first()
    if not d:
        return "failure"
    d.command = command
    db.session.commit()
    return "success"

def hand_cmd(cmd):
    cmd = cmd.lower()
    cmd = ' '.join(cmd.strip().split())
    list_pattern = [r'(b[a|â|ậ]t)\s([d|đ][e|è]n\ss[o|ô|ố]\s[1-9]{1,2})', r'(b[a|â|ậ]t)\s([d|đ][e|è]n\s[1-9]{1,2})']
    list_pattern.extend([r'(t[a|ă|ắ]t)\s([d|đ][e|è]n\ss[o|ô|ố]\s[1-9]{1,2})', r'(t[a|ă|ắ]t)\s([d|đ][e|è]n\s[1-9]{1,2})'])
    for pattern in list_pattern:
        match = re.search(pattern, cmd)
        if match:
            return match.group(1), match.group(2)
    return '', ''


@app.route('/add-device', methods=['POST'])
def add():
    msg_received = request.get_json()
    name = msg_received["name"]
    stt = msg_received["status"]
    active = msg_received["is_active"]
    d = DeviceDemo.query.filter_by(
        name=name).first()  # if this returns a user, then the email already exists in database
    count = DeviceDemo.query.filter_by().all()
    if d:
        return "This device was exist"
    else:
        new_d = DeviceDemo(name=name, status=stt, id=len(count) + 1, is_active=active, command=-1)
        try:
            # add the new user to the database
            db.session.add(new_d)
            db.session.commit()
            return "success"
        except Exception as e:
            print("Error while inserting the new record :", repr(e))
            return "failure"

@app.route('/del-device-by-name', methods = ['POST'])
def delete_by_name():
    msg_received = request.get_json()
    name = msg_received["name"]
    d = DeviceDemo.query.filter_by(
        name=name).first()  # if this returns a user, then the email already exists in database
    if d:
        db.session.delete(d)
        db.session.commit()
        return "success"
    return "The device is not exist!"


@app.route('/', methods = ['POST'])
def chat():
    msg_received = request.get_json()
    print(msg_received)
    msg_subject = msg_received["subject"]
    print(msg_subject)
    if msg_subject == "register":
        return register(msg_received)
    elif msg_subject == "login":
        return login(msg_received)
    elif msg_subject == 'logout':
        return logout(msg_received)
    else:
        return "Invalid request."

def register(msg_received):
    username = msg_received["username"]
    password = msg_received["password"]

    user = User.query.filter_by(name=username).first() # if this returns a user, then the email already exists in database
    count = User.query.filter_by().all()

    if user:  # if a user is found, we want to redirect back to signup page so user can try again
        return "username"
    else:
        # create new user with the form data. Hash the password so plaintext version isn't saved.
        new_user = User(name=username, pwd=generate_password_hash(password, method='sha256'), id=len(count) + 1)
        try:
            # add the new user to the database
            db.session.add(new_user)
            db.session.commit()
            return "success"
        except Exception as e:
            print("Error while inserting the new record :", repr(e))
            return "failure"


def login(msg_received):
    username = msg_received["username"]
    password = msg_received["password"]

    user = User.query.filter_by(name=username).first()

    if not user or not check_password_hash(user.pwd, password):
        return "failure"
    else:
        if user.is_active:
            return "username"
        user.is_active = 1
        db.session.commit()
        return "success"

def logout(msg_received):
    username = msg_received["username"]
    user = User.query.filter_by(name=username).first()
    if not user:
        return "failure"
    user.is_active = 0
    db.session.commit()
    return "success"


@app.errorhandler(404)
def not_found(error=None):
    message = {
        'status': 404,
        'message': 'Not Found: ' + request.url,
    }
    resp = jsonify(message)
    resp.status_code = 404

    return resp



if __name__ == "__main__":
    app.run(host='192.168.43.103')
    # app.run(threaded=True)
    # print(hand_cmd('Bật đèn 1 lên'))