.. _documentation:

===============
Documentation
===============

Overview - Online Bill Payment System: "My Bill Payments"
==========================================================
Our client, a recognized Fintech named "ABCFintech", wants to offer its current and potential customers an application to pay their bills online. This application is called "My Bill Payments". 

Through this application, a customer gets a centralized platform to pay all their bills from different companies like BC Hydro (Electricity), Rogers (cellphone), Telus (Internet) (from now on, named "billers") as well as manage them, thus saving time and avoiding overdue balances and late fees. 

For example, a person named John Doe wants to pay three bills at the end of the month: BC Hydro, Cellphone (Rogers), and Internet (Telus). John will be able to log in on our application, and through the "Bill retrieval" functionality, he can look up the details of each bill, using as input the biller name and the bill identifier, e.g., Biller name: "Rogers" and Bill ID: 12345. 

Assuming that the bill exists, our system will return the bill's relevant information: Biller name, Bill identifier, amount to pay, and due date. Then, John can pay this bill using his credit card. Hence, John will enter its credit card information into the system: credit card number (16 digits), cardholder name, expiration year-month, and the three-digit security code. In case that John has funds available to cover this payment, our system will flag the bill as paid (preventing that it can be paid again by another user) and register the payment information (credit card first four and last four digits, cardholder name and expiration year-month). 

The user can repeat this process as many times as the number of bills. Also, if John wants to review his paid bills, our system will provide a functionality to review all his past payments (payments history). 

|

Architecture
============

.. image:: /_static/images/architecture.jpg
	:align: center
   	:width: 1400
   	:height: 806

|

Specification of REST API (Microservices Contract)
==================================================

* **Version:** v1
* **Service:** Gateway
* **Visibility:** Public
* **Domain:** Ingress-gateway
* **Serialized Data/Content-Type:** ``json``

+---------------------+--------------------+
| API                 | Description        |
+=====================+====================+
| ``/api/v1/user``    | Users service URL  |
+---------------------+--------------------+
| ``/api/v1/bill``    | Bills service URL  |
+---------------------+--------------------+
| ``/api/v1/billers`` | Billers service URL|
+---------------------+--------------------+

|

Service: ``Users``
===================

* **Version:** v1
* **Visibility:** Private
* **Domain:** Users
* **Serialized Data/Content-Type:** ``json``

.. image:: /_static/images/users.jpg
	:align: center
   	:width: 1812
   	:height: 400

|

Service: ``Bill``
==================

* **Version:** v1
* **Visibility:** Private
* **Domain:** Bills
* **Serialized Data/Content-Type:** ``json``

.. image:: /_static/images/bill.jpg
	:align: center
   	:width: 1788
   	:height: 358

|

Service: ``Biller``
===================

* **Version:** v1
* **Visibility:** Private
* **Domain:** Billers
* **Serialized Data/Content-Type:** ``json``

.. image:: /_static/images/biller.jpg
	:align: center
   	:width: 1787
   	:height: 221

|

Service: ``db``
===============

* **Version:** v1
* **Visibility:** Private
* **Domain:** Datastore
* **Serialized Data/Content-Type:** ``json``

.. image:: /_static/images/db.jpg
	:align: center
   	:width: 1880
   	:height: 574

|

Database Schema (DynamoDB)
===========================	

+-----------------------+---------+-------------------------------------------------+
| Table: Users                                                                      |
+=======================+=========+=================================================+
| **Tag**               |**Value**| **Comment**                                     |
+-----------------------+---------+-------------------------------------------------+
| ``lname``             |*string* | Last name of user                               |
+-----------------------+---------+-------------------------------------------------+
| ``fname``             |*string* | First name of user                              |
+-----------------------+---------+-------------------------------------------------+
| ``uname``             |*string* | Email ID of user                                |
+-----------------------+---------+-------------------------------------------------+
| ``password``          |*string* | User’s account password                         |
+-----------------------+---------+-------------------------------------------------+
| ``security_question`` |*string* | Security question in case user forgets password |
+-----------------------+---------+-------------------------------------------------+
| ``security_answer``   |*string* | Answer to the security question                 |
+-----------------------+---------+-------------------------------------------------+
| ``user_id``           |*string* | Unique id generated by DynamoDB                 |
+-----------------------+---------+-------------------------------------------------+

|

+--------------------------+---------+---------------------------------------------------------------+
| Table: Bills                                                                                       |
+==========================+=========+===============================================================+
| **Tag**                  |**Value**| **Comment**                                                   |
+--------------------------+---------+---------------------------------------------------------------+
| ``bill_amount``          |*decimal*| Amount billed                                                 |
+--------------------------+---------+---------------------------------------------------------------+
| ``bill_paid``            |*boolean*| true/false                                                    |
+--------------------------+---------+---------------------------------------------------------------+
| ``due_date``             |*string* | Due date for the bill                                         |
+--------------------------+---------+---------------------------------------------------------------+
| ``payment_date``         |*string* | Date of the bill payment                                      |
+--------------------------+---------+---------------------------------------------------------------+
| ``cc_first_four_digits`` |*integer*| Identification of credit card franchise: Visa, MasterCard etc |
+--------------------------+---------+---------------------------------------------------------------+
| ``cc_last_four_digits``  |*integer*| Credit card information for troubleshooting                   |
+--------------------------+---------+---------------------------------------------------------------+
| ``cc_exp_date``          |*string* | Expiry date of credit card                                    |
+--------------------------+---------+---------------------------------------------------------------+
| ``user_id``              |*string* | User id                                                       |
+--------------------------+---------+---------------------------------------------------------------+
| ``biller_id``            |*string* | Biller id which issued the bill                               |
+--------------------------+---------+---------------------------------------------------------------+
| ``bill_id``              |*string* | Bill id                                                       |
+--------------------------+---------+---------------------------------------------------------------+

|

+-----------------------+---------+------------------------------------------------+
| Table: Billers                                                                   |
+=======================+=========+================================================+
| **Tag**               |**Value**| **Comment**                                    |
+-----------------------+---------+------------------------------------------------+
| ``biller_name``       |*string* | Name of the billing company                    |
+-----------------------+---------+------------------------------------------------+
| ``active``            |*boolean*| true/false                                     |
+-----------------------+---------+------------------------------------------------+
| ``biller_description``|*string* | Description of the billing company             |
+-----------------------+---------+------------------------------------------------+
| ``biller_id``         |*string* | Unique id not interpreted by DynamoDB          |
+-----------------------+---------+------------------------------------------------+
