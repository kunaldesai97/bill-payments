from flask import Flask
import requests
import sys
import logging
import simplejson as json
import urllib
from flask import request
from flask import Response
from flask import Blueprint
from datetime import datetime

app = Flask(__name__)

db = {
    "name": "http://dbcontainer:30002/api/v1/datastore",
    "endpoint": [
        "read",
        "write",
        "delete",
        "update",
        "scan",
    ]
}

# Create a Flask blueprint
bill_bp = Blueprint('app', __name__)

@bill_bp.route('/health')
def health():
    return Response("", status=200, mimetype="application/json")

@bill_bp.route('/readiness')
def readiness():
    return Response("", status=200, mimetype="application/json")


@bill_bp.route('/<bill_id>', methods=['GET'])
def get_bill(bill_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "Permission denied. You can't retrieve the following bill without authorization."}), status=401, mimetype='application/json')
    payload = {"objtype": "bill", "objkey": bill_id}
    url = db['name'] + '/' + db['endpoint'][0]
    response = requests.get(url, params = payload, headers = {'Authorization': headers['Authorization']})
    return (response.json())


@bill_bp.route('/', methods=['POST'])
def create_bill():
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "You don't have permission to generate a bill. Please use authorization token."}), status=401, mimetype='application/json')
    try:
        content = request.get_json()
        user_id = content['user_id']
        biller_id = content['biller_id']
        bill_amount = content['bill_amount']
        due_date = content['due_date']
        bill_paid = content['bill_paid']
    except: 
        return json.dumps({"message": "Error reading arguments"})
    url = db['name'] + '/' + db['endpoint'][1]
    response = requests.post(url, json = {"objtype": "bill", "user_id":user_id, "biller_id":biller_id, "bill_amount": bill_amount,"due_date":due_date,"bill_paid":bill_paid}, headers = {'Authorization': headers['Authorization']})
    return (response.json())


@bill_bp.route('/<bill_id>', methods=['DELETE'])
def delete_bill(bill_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "Permission denied due to missing authorization token."}), status=401, mimetype='application/json')
    url = db['name'] + '/' + db['endpoint'][2]
    response = requests.delete(url, params = { "objtype": "bill", "objkey": bill_id}, headers = {'Authorization': headers['Authorization']})
    return (response.json())


@bill_bp.route('/pay/<bill_id>', methods=['PUT'])
def pay_bill(bill_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "Authorize to pay bills"}), status=401, mimetype='application/json')
    try:
        content = request.get_json()
        cc_first_four_digits = content['cc_number'][:4]
        cc_last_four_digits = content['cc_number'][-4:]
        cc_exp_date = content['cc_exp_date']
        payment_date = str(datetime.today().strftime("%B %d %Y"))
        bill_paid = content['bill_paid']
    except:
        return json.dumps({"message": "Error reading arguments"})
    url = db['name'] + '/' + db['endpoint'][3]
    response = requests.put(url, params = {"objtype": "bill", "objkey": bill_id}, json={"cc_first_four_digits":cc_first_four_digits,"cc_last_four_digits":cc_last_four_digits,"cc_exp_date":cc_exp_date,"payment_date":payment_date,"bill_paid":bill_paid},
                             headers={'Authorization': headers['Authorization']})
    return (response.json())


@bill_bp.route('/users/<user_id>', methods=['GET'])
def get_bills_user(user_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "Permission denied. You can't retrieve the following bill without authorization."}), status=401, mimetype='application/json')
    payload = {"objtype": "bill", "objkey": user_id}
    url = db['name'] + '/' + db['endpoint'][4]
    response = requests.get(url, params = payload, headers = {'Authorization': headers['Authorization']})
    return (response.json())


# Register the created blueprint
app.register_blueprint(bill_bp, url_prefix='/api/v1/bill/')

if __name__ == '__main__':
    if len(sys.argv) < 2:
        logging.error("missing port arg 1")
        sys.exit(-1)

    p = int(sys.argv[1])
    app.run(host='0.0.0.0', port=p, threaded=True)
