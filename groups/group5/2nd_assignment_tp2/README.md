# BCC362TP2-python


# How to run

### open folder ../src/

Run on each machine: 
python3 broker.py <IP_ADDRESS> < PORT> <machine_name>
* Broker

> python3 broker.py 10.158.0.5 8080

* M1

> python3 client.py 10.158.0.5 8080 10.158.0.2 8081 maquina1

* M2

>python3 client.py 10.158.0.5 8080 10.158.0.4 8081 maquina2

* M3

> python3 client.py 10.158.0.5 8080 10.158.0.3 8081 maquina3

* M4

> python3 client.py 10.158.0.5 8080 10.158.0.6 8081 maquina4
