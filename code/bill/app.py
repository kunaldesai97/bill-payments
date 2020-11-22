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
    "name": "http://<DB_NAME>:30002/api/v1/datastore",
    "endpoint": [
        "read",
        "write",
        "delete",
        "update",
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
        User_ID = content['user_id']
        Bill_ID = content['bill_id']
        Biller_ID = content['biller_id']
        Bill_Amount = content['bill_amount']
        Due_Date = content['due_date']
        # By default making Is_Bill_Paid false as soon as bill is generated
        Is_Bill_Paid = False
    except: 
        return json.dumps({"message": "Error reading arguments"})
    url = db['name'] + '/' + db['endpoint'][1]
    response = requests.post(url, json = {"objtype": "bill", "UserID":User_ID,"BillID":Bill_ID, "BillerID":Biller_ID, "Amount": Bill_Amount,"DueDate":Due_Date,"BillStatus":Is_Bill_Paid}, headers = {'Authorization': headers['Authorization']})
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
        #Bill_ID = content['bill_id']
        Biller_ID = content['biller_id']
        CC_Number = content['cc_number']
        CC_EXP_DATE = content['cc_exp_date']
        CC_CVV = content['cc_cvv']
        # By default making it true as soon as bill is paid
        Is_Bill_Paid = True
    except:
        return json.dumps({"message": "Error reading arguments"})
    url = db['name'] + '/' + db['endpoint'][3]
    response = requests.put(url, json={"objtype": "bill", "BillID": bill_id, "BillerID": Biller_ID, "CC_Number": CC_Number,
                                        "CC_EXP_DATE": CC_EXP_DATE, "CC_CVV": CC_CVV, "BillStatus":Is_Bill_Paid},
                             headers={'Authorization': headers['Authorization']})
    return (response.json())

"""
@bill_bp.route('/<user_id>', methods=['GET'])
def get_bills_user(user_id):
    headers = request.headers
    # check header here
    if 'Authorization' not in headers:
        return Response(json.dumps({"error": "Permission denied. You can't retrieve the following bill without authorization."}), status=401, mimetype='application/json')
    payload = {"objtype": "bill", "objkey": bill_id}
    url = db['name'] + '/' + db['endpoint'][0]
    response = requests.get(url, params = payload, headers = {'Authorization': headers['Authorization']})
    for bill in response['Items']:
        return (bill[user_id])
"""

# Register the created blueprint
app.register_blueprint(bill_bp, url_prefix='/api/v1/bill/')

if __name__ == '__main__':
    if len(sys.argv) < 2:
        logging.error("missing port arg 1")
        sys.exit(-1)

    p = int(sys.argv[1])
    app.run(host='0.0.0.0', port=p, threaded=True)
