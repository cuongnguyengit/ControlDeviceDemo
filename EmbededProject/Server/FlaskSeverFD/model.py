from flask_login import UserMixin
from __init__ import db

class User(db.Model):
    id = db.Column(db.Integer, autoincrement=True)  # primary keys are required by SQLAlchemy
    # email = db.Column(db.String(100), unique=True)
    pwd = db.Column(db.String(100))
    name = db.Column(db.String(1000), primary_key=True)
    is_active = db.Column(db.Boolean(), default=False)

    def __repr__(self):
        return '<User %r>' % (self.name)


class DeviceDemo(db.Model):
    id = db.Column(db.Integer, autoincrement=True)  # primary keys are required by SQLAlchemy
    # email = db.Column(db.String(100), unique=True)
    status = db.Column(db.Boolean(), default=False)
    name = db.Column(db.String(1000), primary_key=True)
    is_active = db.Column(db.Boolean(), default=False)
    command = db.Column(db.Integer, default=-1)
