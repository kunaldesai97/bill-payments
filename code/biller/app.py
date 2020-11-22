from flask import Flask
import requests
import sys
import logging
import simplejson as json
import urllib
from flask import request
from flask import Response
from flask import Blueprint
app = Flask(__name__)

db = {
    "name": "http://projectdb:30002/api/v1/datastore",
    "endpoint": [
        "read",
        "write",
        "delete"
    ]
}
bp = Blueprint('app', __name__)
@bp.route('/health')
def health():
    return Response("", status=200, mimetype="application/json")

@bp.route('/readiness')
def readiness():
    return Response("", status=200, mimetype="application/json")

@bp.route('/', methods=['GET'])
def list_all():
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "missing auth"}), status=401, mimetype='application/json')
    # list all songs here
    return {}

@bp.route('/<biller_id>', methods=['GET'])
def get_biller(music_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "missing auth"}), status=401, mimetype='application/json')
    payload = {"objtype": "biller", "objkey": biller_id}
    url = db['name'] + '/' + db['endpoint'][0]
    response = requests.get(url, params = payload, headers = {'Authorization': headers['Authorization']})
    return (response.json())

@bp.route('/', methods=['POST'])
def create_biller():
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "missing auth"}), status=401, mimetype='application/json')
    try:
        content = request.get_json()
        Biller = content['Biller']
        Description = content['Description']

    except: 
        return json.dumps({"message": "error reading arguments"})
    url = db['name'] + '/' + db['endpoint'][1]
    response = requests.post(url, json = {"objtype": "biller", "Biller":Biller, "Description": Description}, headers = {'Authorization': headers['Authorization']})
    return (response.json())

@bp.route('/<biller_id>', methods=['DELETE'])
def delete_biller(biller_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "missing auth"}), status=401, mimetype='application/json')
    url = db['name'] + '/' + db['endpoint'][2]
    response = requests.delete(url, params = { "objtype": "biller", "objkey": biller_id}, headers = {'Authorization': headers['Authorization']})
    return (response.json())

app.register_blueprint(bp, url_prefix='/api/v1/biller/')

if __name__ == '__main__':
    if len(sys.argv) < 2:
        logging.error("missing port arg 1")
        sys.exit(-1)

    p = int(sys.argv[1])
    app.run(host='0.0.0.0', port=p, threaded=True)
