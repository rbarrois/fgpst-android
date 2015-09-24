import flask
from flask import request
import sys

app = flask.Flask(__name__)

@app.route('/', methods=['POST'])
def show_data():
    sys.stdout.write("args: %r; data: %r\n" % (request.args, request.data))
    return 'OK'


if __name__ == '__main__':
    app.run('::', 8000)
