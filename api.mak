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
# s1: service1; s2: service2; db: cmpt756db
PODS1=pod/billservice-57c6994db6-lhmn6
PODCONT=service1

logs:
	$(KC) logs $(PODS1) -c $(PODCONT)

#
# Replace this with the external IP/DNS name of your cluster
#
# In all cases, look up the external IP of the istio-ingressgateway LoadBalancer service
# You can use either 'make -f eks.m extern' or 'make -f mk.m extern' or
# directly 'kubectl -n istio-system get service istio-ingressgateway'
#
#IGW=172.16.199.128:31413
IGW=10.108.215.107:80
#IGW=aa950744aa9db4c21b66eece5344f695-1630890617.us-east-1.elb.amazonaws.com:80
#IGW= 20.151.89.30:80

# stock body & fragment for API requests
BODY_USER= { \
"fname": "Homer1", \
"email": "shomer@simpsons.org", \
"lname": "Simpsons1" \
}


BODY_BILL= { \
"userid": "rohan@sfu.ca", \
"billerid": "bchydro@cer.org", \
"bill_amount": "50", \
"due_date": "23 March", \
"bill_paid": "True" \
}

PAY_BILL={ \
"BillID": "bchydro@cer.org", \
"cc_number": "123456789", \
"cc_exp_date": "23 March 2021", \
"cc_cvv": "123" \
}

BODY_UID= { \
    "uid": "74915a10-4c3b-4d88-8bd0-d999f140ed2a" \
}

BODY_MUSIC= { \
  "Artist": "Taylor Swift", \
  "SongTitle": "Style" \
}

BODY_BILLER= { \
  "Biller": "BC Hydro", \
  "Description": "Electricity" \
}

# this is a token for ???
TOKEN=Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMDI3Yzk5ZWYtM2UxMi00ZmM5LWFhYzgtMTcyZjg3N2MyZDI0IiwidGltZSI6MTYwMTA3NDY0NC44MTIxNjg2fQ.hR5Gbw5t2VMpLcj8yDz1B6tcWsWCFNiHB_KHpvQVNls
BODY_TOKEN={ \
    "jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNzQ5MTVhMTAtNGMzYi00ZDg4LThiZDAtZDk5OWYxNDBlZDJhIiwidGltZSI6MTYwNDA0Mzk2MS44ODU5NjZ9.cCsA5iMZG6TQ_DeCP5mAdT_I5b2mSz5mhCh5gp1r1jU" \
}

# keep these ones around
USER_ID=f84a897c-cb15-474a-a10c-41d56664fc4f
MUSIC_ID=2995bc8b-d872-4dd1-b396-93fde2f4bfff
BILL_ID=8b0250d4-3596-44ec-a4dc-1bdd1fa61bc9
# it's convenient to have a second set of id to test deletion (DELETE uses these id with the suffix of 2)
USER_ID2=011c55f7-448a-4cda-aff1-bc32359f03fd
MUSIC_ID2=3956ff07-7bdc-4110-b4b7-9acf1d489cd3

# POST is used for user (apipost) or music (apimusic) to create a new record
cuser:
	echo curl --location --request POST 'http://$(IGW)/api/v1/user/' --header 'Content-Type: application/json' --data-raw '$(BODY_USER)' > cuser.out
	$(CURL) --location --request POST 'http://$(IGW)/api/v1/user/' --header 'Content-Type: application/json' --data-raw '$(BODY_USER)' | tee -a cuser.out

cbill:
	echo curl --location --request POST 'http://$(IGW)/api/v1/bill/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_BILL)' > cbill.out
	$(CURL) --location --request POST 'http://$(IGW)/api/v1/bill/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_BILL)' | tee -a cbill.out

gbill:
	echo curl --location --request GET 'http://$(IGW)/api/v1/bill/$(BILL_ID)' --header '$(TOKEN)' > gbill.out
	$(CURL) --location --request GET 'http://$(IGW)/api/v1/bill/$(BILL_ID)' --header '$(TOKEN)' | tee -a gbill.out

pbill:
	echo curl --location --request GET 'http://$(IGW)/api/v1/bill/pay/$(BILL_ID)' --header '$(TOKEN)' > pbill.out
	$(CURL) --location --request GET 'http://$(IGW)/api/v1/bill/pay/$(BILL_ID)' --header '$(TOKEN)' | tee -a pbill.out

dbill:
	echo curl --location --request DELETE 'http://$(IGW)/api/v1/bill/$(BILL_ID)' --header '$(TOKEN)' > dbill.out
	$(CURL) --location --request DELETE 'http://$(IGW)/api/v1/bill/$(BILL_ID)' --header '$(TOKEN)' | tee -a dbill.out

cbiller:
	echo curl --location --request POST 'http://$(IGW)/api/v1/biller/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_BILLER)' > cbiller.out
	$(CURL) --location --request POST 'http://$(IGW)/api/v1/biller/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_BILLER)' | tee -a cbiller.out

cmusic:
	echo curl --location --request POST 'http://$(IGW)/api/v1/music/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_MUSIC)' > cmusic.out
	$(CURL) --location --request POST 'http://$(IGW)/api/v1/music/' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_MUSIC)' | tee -a cmusic.out

# PUT is used for user (update) to update a record
uuser:
	echo curl --location --request PUT 'http://$(IGW)/api/v1/user/$(USER_ID)' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_USER)' > uuser.out
	$(CURL) --location --request PUT 'http://$(IGW)/api/v1/user/$(USER_ID)' --header '$(TOKEN)' --header 'Content-Type: application/json' --data-raw '$(BODY_USER)' | tee -a uuser.out

# GET is used with music to read a record
rmusic:
	echo curl --location --request GET 'http://$(IGW)/api/v1/music/$(MUSIC_ID)' --header '$(TOKEN)' > rmusic.out
	$(CURL) --location --request GET 'http://$(IGW)/api/v1/music/$(MUSIC_ID)' --header '$(TOKEN)' | tee -a rmusic.out

# DELETE is used with user or music to delete a record
duser:
	echo curl --location --request DELETE 'http://$(IGW)/api/v1/user/$(USER_ID2)' --header '$(TOKEN)' > duser.out
	$(CURL) --location --request DELETE 'http://$(IGW)/api/v1/user/$(USER_ID2)' --header '$(TOKEN)' | tee -a duser.out

dmusic:
	echo curl --location --request DELETE 'http://$(IGW)/api/v1/music/$(MUSIC_ID2)' --header '$(TOKEN)' > dmusic.out
	$(CURL) --location --request DELETE 'http://$(IGW)/api/v1/music/$(MUSIC_ID2)' --header '$(TOKEN)' | tee -a dmusic.out

# PUT is used for login/logoff too
apilogin:
	echo curl --location --request PUT 'http://$(IGW)/api/v1/user/login' --header 'Content-Type: application/json' --data-raw '$(BODY_UID)' > apilogin.out
	$(CURL) --location --request PUT 'http://$(IGW)/api/v1/user/login' --header 'Content-Type: application/json' --data-raw '$(BODY_UID)' | tee -a apilogin.out

apilogoff:
	echo curl --location --request PUT 'http://$(IGW)/api/v1/user/logoff' --header 'Content-Type: application/json' --data-raw '$(BODY_TOKEN)' > apilogoff.out
	$(CURL) --location --request PUT 'http://$(IGW)/api/v1/user/logoff' --header 'Content-Type: application/json' --data-raw '$(BODY_TOKEN)' | tee -a apilogoff.out
