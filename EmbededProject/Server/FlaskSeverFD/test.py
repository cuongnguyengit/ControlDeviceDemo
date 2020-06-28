import speech_recognition as sr
import numpy as np
from scipy.io import wavfile
import soundfile as sf

def convert_standard_wav(wav_path, new_path=None):
    data, fs = sf.read(wav_path, subtype='BYTE')
    # Convert `data` to 32 bit integers:
    y = (np.iinfo(np.int32).max * (data/np.abs(data).max())).astype(np.int32)
    if new_path is None:
        wavfile.write(wav_path.replace('.wav', '_32.wav'), fs, y)
    else:
        wavfile.write(new_path, fs, y)

def recognition_with_mic():
    # Sample rate is how often values are recorded
    sample_rate = 48000
    chunk_size = 2048
    # Initialize the recognizer
    r = sr.Recognizer()
    # generate a list of all audio cards/microphones
    mic_list = sr.Microphone.list_microphone_names()
    device_id = -1
    for i, microphone_name in enumerate(mic_list):
        if 'microphone' in microphone_name.lower():
            device_id = i
            break
    with sr.Microphone(device_index=device_id, sample_rate=sample_rate,
                       chunk_size=chunk_size) as source:
        r.adjust_for_ambient_noise(source)
        print("Say Something")
        # listens for the user's input
        audio = r.listen(source, timeout=3, phrase_time_limit=10)
        text = ''
        try:
            text += r.recognize_google(audio, language='vi-VN')
            print("you said: " + text)
            # error occurs when google could not understand what was said
        except sr.UnknownValueError:
            print("Google Speech Recognition could not understand audio")
        except sr.RequestError as e:
            print("Could not request results from Google Speech Recognition service;{0}".format(e))
        return text


def recognition_with_wav(wav_path):
    r = sr.Recognizer()
    with sr.WavFile(wav_path) as source:
        audio = r.record(source)
        r.adjust_for_ambient_noise(source)
        text = ''
        try:
            text += r.recognize_google(audio, language='vi-VN')
            print("you said: " + text)
            # error occurs when google could not understand what was said
        except sr.UnknownValueError:
            print("Google Speech Recognition could not understand audio")
        except sr.RequestError as e:
            print("Could not request results from Google Speech Recognition service;{0}".format(e))
        return text


if __name__ == '__main__':
    # convert_standard_wav('voice.wav', 'voice.wav')
    recognition_with_wav('voice.wav')
