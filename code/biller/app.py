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
    "name": "http://dbcontainer:30002/api/v1/datastore",
    "endpoint": [
        "read",
        "write",
        "delete",
    ]
}

# Create a Flask blueprint
biller_bp = Blueprint('app', __name__)

@biller_bp.route('/health')
def health():
    return Response("", status=200, mimetype="application/json")

@biller_bp.route('/readiness')
def readiness():
    return Response("", status=200, mimetype="application/json")


@biller_bp.route('/<biller_id>', methods=['GET'])
def get_biller(biller_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "Permission denied. You can't retrieve the following bill without authorization."}), status=401, mimetype='application/json')
    payload = {"objtype": "biller", "objkey": biller_id}
    url = db['name'] + '/' + db['endpoint'][0]
    response = requests.get(url, params = payload, headers = {'Authorization': headers['Authorization']})
    return (response.json())


@biller_bp.route('/', methods=['POST'])
def create_biller():
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "You don't have permission to generate a bill. Please use authorization token."}), status=401, mimetype='application/json')
    try:
        content = request.get_json()
        biller = content['biller']
        description = content['description']
    except: 
        return json.dumps({"message": "Error reading arguments"})
    url = db['name'] + '/' + db['endpoint'][1]
    response = requests.post(url, json = {"objtype": "biller", "biller": biller, "description": description}, headers = {'Authorization': headers['Authorization']})
    return (response.json())


@biller_bp.route('/<biller_id>', methods=['DELETE'])
def delete_biller(biller_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "Permission denied due to missing authorization token."}), status=401, mimetype='application/json')
    url = db['name'] + '/' + db['endpoint'][2]
    response = requests.delete(url, params = { "objtype": "biller", "objkey": biller_id}, headers = {'Authorization': headers['Authorization']})
    return (response.json())


# Register the created blueprint
app.register_blueprint(biller_bp, url_prefix='/api/v1/biller/')

if __name__ == '__main__':
    if len(sys.argv) < 2:
        logging.error("missing port arg 1")
        sys.exit(-1)

    p = int(sys.argv[1])
    app.run(host='0.0.0.0', port=p, threaded=True)
