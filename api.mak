#
# Janky front-end to bring some sanity (?) to the litany of tools and switches
# in setting up, tearing down and validating your Minikube cluster for working
# with k8s and istio.
#
# This file covers off driving the API independent of where the cluster is
# running.
# Be sure to set your context appropriately for the log monitor.
#
# The intended approach to working with this makefile is to update select
# elements (body, id, IP, port, etc) as you progress through your workflow.
# Where possible, stodout outputs are tee into .out files for later review.
#


KC=kubectl
CURL=curl

# look these up with 'make ls'
# You need to specify the container because istio injects side-car container
# into each pod.
# s1: service1; s2: service2; s3: service3, db: dbcontainer
PODS1=pod/billservice-57c6994db6-lhmn6
PODCONT=service1

PODS2=pod/billercontainer-678d867b47-n2r89
PODCONT=service3

logs:
	$(KC) logs $(PODS2) -c $(PODCONT)

#
# Replace this with the external IP/DNS name of your cluster
#
# In all cases, look up the external IP of the istio-ingressgateway LoadBalancer service
# You can use either 'make -f eks.m extern' or 'make -f mk.m extern' or
# directly 'kubectl -n istio-system get service istio-ingressgateway'

# Minikube External IP
IGW=10.108.29.202:80

# AWS External IP	
#IGW=aa950744aa9db4c21b66eece5344f695-1630890617.us-east-1.elb.amazonaws.com:80

# Azure External IP
#IGW=52.149.246.230:80

# stock body & fragment for API requests
BODY_USER= { \
"fname": "John New", \
"uname": "john.doe@test.org", \
"lname": "doe1", \
"secquestion": "pet animal", \
"secanswer": "duck" \
}


BODY_BILL= { \
"user_id": "test@gmail.com", \
"biller_id": "telus@can.org", \
"bill_amount": "50", \
"due_date": "March 25 2021", \
"bill_paid": "false" \
}

PAY_BILL={ \
"cc_number": "450556789349966", \
"cc_exp_date": "April 5 2024", \
"bill_paid": "true" \
}


BODY_UID= { \
    "uid": "74915a10-4c3b-4d88-8bd0-d999f140ed2a" \
}

BODY_BILLER= { \
  "biller": "BC Hydro1", \
  "description": "Electricity1" \
}

# this is a token for ???
TOKEN=Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMDI3Yzk5ZWYtM2UxMi00ZmM5LWFhYzgtMTcyZjg3N2MyZDI0IiwidGltZSI6MTYwMTA3NDY0NC44MTIxNjg2fQ.hR5Gbw5t2VMpLcj8yDz1B6tcWsWCFNiHB_KHpvQVNls
BODY_TOKEN={ \
    "jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNzQ5MTVhMTAtNGMzYi00ZDg4LThiZDAtZDk5OWYxNDBlZDJhIiwidGltZSI6MTYwNDA0Mzk2MS44ODU5NjZ9.cCsA5iMZG6TQ_DeCP5mAdT_I5b2mSz5mhCh5gp1r1jU" \
}

# Used in user apis
USER_ID=0f41e85c-4bad-479c-9ec9-bc2846dfb98c

# Used in bill apis
BILL_ID=52de2849-cab1-4b12-9bfe-0230a4756dd8

# Used in biller apis
BILLER_ID = 534eb082-49f2-4c2e-9186-14f67161b8f5

# Used for getting a user's bill in guserbill api
USER_ID2=123

# CREATE a bill
cbill:
	echo curl --location --request POST 'http://$(IGW)/api/v1/bill/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_BILL)' > cbill.out
	$(CURL) --location --request POST 'http://$(IGW)/api/v1/bill/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_BILL)' | tee -a cbill.out

# GET a bill
gbill:
	echo curl --location --request GET 'http://$(IGW)/api/v1/bill/$(BILL_ID)' --header '$(TOKEN)' > gbill.out
	$(CURL) --location --request GET 'http://$(IGW)/api/v1/bill/$(BILL_ID)' --header '$(TOKEN)' | tee -a gbill.out

# PAY a bill
pbill:
	echo curl --location --request PUT 'http://$(IGW)/api/v1/bill/pay/$(BILL_ID)' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(PAY_BILL)'> pbill.out
	$(CURL) --location --request PUT 'http://$(IGW)/api/v1/bill/pay/$(BILL_ID)' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(PAY_BILL)'| tee -a pbill.out

# DELETE a bill
dbill:
	echo curl --location --request DELETE 'http://$(IGW)/api/v1/bill/$(BILL_ID)' --header '$(TOKEN)' > dbill.out
	$(CURL) --location --request DELETE 'http://$(IGW)/api/v1/bill/$(BILL_ID)' --header '$(TOKEN)' | tee -a dbill.out

# GET a user's bill
guserbill:
	echo curl --location --request GET 'http://$(IGW)/api/v1/bill/users/$(USER_ID2)' --header '$(TOKEN)' > userbills.out
	$(CURL) --location --request GET 'http://$(IGW)/api/v1/bill/users/$(USER_ID2)' --header '$(TOKEN)' | tee -a userbills.out

# CREATE a biller
cbiller:
	echo curl --location --request POST 'http://$(IGW)/api/v1/biller/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_BILLER)' > cbiller.out
	$(CURL) --location --request POST 'http://$(IGW)/api/v1/biller/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_BILLER)' | tee -a cbiller.out

# DELETE a biller
dbiller:
	echo curl --location --request DELETE 'http://$(IGW)/api/v1/biller/$(BILLER_ID)' --header '$(TOKEN)' > dbiller.out
	$(CURL) --location --request DELETE 'http://$(IGW)/api/v1/biller/$(BILLER_ID)' --header '$(TOKEN)' | tee -a dbiller.out

# GET a biller
gbiller:
	echo curl --location --request GET 'http://$(IGW)/api/v1/biller/$(BILLER_ID)' --header '$(TOKEN)' > gbiller.out
	$(CURL) --location --request GET 'http://$(IGW)/api/v1/biller/$(BILLER_ID)' --header '$(TOKEN)' | tee -a gbiller.out

# CREATE a user
cuser:
	echo curl --location --request POST 'http://$(IGW)/api/v1/user/' --header 'Content-Type: application/json' --data-raw '$(BODY_USER)' > cuser.out
	$(CURL) --location --request POST 'http://$(IGW)/api/v1/user/' --header 'Content-Type: application/json' --data-raw '$(BODY_USER)' | tee -a cuser.out

# GET a user
ruser:
	echo curl --location --request GET 'http://$(IGW)/api/v1/user/$(USER_ID)' --header '$(TOKEN)' > ruser.out
	$(CURL) --location --request GET 'http://$(IGW)/api/v1/user/$(USER_ID)' --header '$(TOKEN)' | tee -a ruser.out

# UPDATE a user
uuser:
	echo curl --location --request PUT 'http://$(IGW)/api/v1/user/$(USER_ID)' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_USER)' > uuser.out
	$(CURL) --location --request PUT 'http://$(IGW)/api/v1/user/$(USER_ID)' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_USER)' | tee -a uuser.out

# DELETE a user
duser:
	echo curl --location --request DELETE 'http://$(IGW)/api/v1/user/$(USER_ID)' --header '$(TOKEN)' > duser.out
	$(CURL) --location --request DELETE 'http://$(IGW)/api/v1/user/$(USER_ID)' --header '$(TOKEN)' | tee -a duser.out

# LOGIN as a user
apilogin:
	echo curl --location --request PUT 'http://$(IGW)/api/v1/user/login' --header 'Content-Type: application/json' --data-raw '$(BODY_UID)' > apilogin.out
	$(CURL) --location --request PUT 'http://$(IGW)/api/v1/user/login' --header 'Content-Type: application/json' --data-raw '$(BODY_UID)' | tee -a apilogin.out

# LOGOFF user
apilogoff:
	echo curl --location --request PUT 'http://$(IGW)/api/v1/user/logoff' --header 'Content-Type: application/json' --data-raw '$(BODY_TOKEN)' > apilogoff.out
	$(CURL) --location --request PUT 'http://$(IGW)/api/v1/user/logoff' --header 'Content-Type: application/json' --data-raw '$(BODY_TOKEN)' | tee -a apilogoff.out
