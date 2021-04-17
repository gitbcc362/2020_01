import requests
import threading
import re
import json
import time
from kafka import KafkaProducer
from bs4 import BeautifulSoup

producer = KafkaProducer(bootstrap_servers='node-3:9093', value_serializer=lambda v: json.dumps(v).encode('utf-8'))

PAGE = 1

HEADERS = {
    'origin': 'https://careers.ibm.com',
    'accept-encoding': 'gzip, deflate, br',
    'accept-language': 'en-US,en;q=0.9',
    'user-agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36',
}
THREADS = []
cont = 0
def get_info(url, country):
    global cont
    try:
        soup = BeautifulSoup(requests.get(
            url, headers=HEADERS).text, "html.parser")
        cont+=1
        print(cont)
        job = {}
        job['url'] = url

        # Title
        job['title'] = soup.find("div", {"class": "job-main"}).h1.text
        job['country'] = country
        job['description'] = "".join(str(soup.find("div", {"id": "job-description"})).rstrip("\n\r")) + "".join(str(soup.find("div", {"id": "additional-fields"})).rstrip("\n\r")) + "".join([str(specs).rstrip("\n\r") for specs in soup.findAll("div", {"class": ["job-specs", "desktop-only"]})])
        job['cont'] = cont
        producer.send('India', job)

    except Exception as E:
        pass

def get_jobs(baseUrl, country):
    PAGE = 1
    while True:
        try:
            searchJobsUrl = baseUrl + str(PAGE) + "/?lang=en"
            has_jobs = False
            response = requests.get(searchJobsUrl, headers=HEADERS)
            soup = BeautifulSoup(response.text, "html.parser")

            for a in soup.find_all("a"):
                if a.parent.name == 'td':
                    has_jobs = True
                    href = a.get("href")
                    url = "https://careers.ibm.com" + href

                    if re.match(r'.*ShowJob\/Id\/.*',href):
                        job = threading.Thread(target=get_info, args=(url,country))
                        job.start()
                        THREADS.append(job)

            if not has_jobs:
                break
            PAGE += 1
        except:
            pass
    # Waiting until all THREADS are finished
    for thread in THREADS:
        thread.join()
while True:
    print('Coletando India')
    get_jobs("https://careers.ibm.com/ListJobs/All/Search/Country/IN/Page-", "IN")
    time.sleep(2)
    producer.send('India', 'India')
    time.sleep(3)



cont = 0
print('Coletando AR')
get_jobs('https://careers.ibm.com/ListJobs/All/Search/Country/AR/Page-', "Argentina")
cont = 0
print('Coletando IN')
get_jobs("https://careers.ibm.com/ListJobs/All/Search/Country/IN/Page-", "IN")
cont = 0
print('coletando USA')
get_jobs("https://careers.ibm.com/ListJobs/All/Search/Country/US/Page-", "USA")
cont = 0
print('aqui')
