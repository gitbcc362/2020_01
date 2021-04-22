FROM python:3.8

# Sets the working directory for following COPY and CMD instructions
# Notice we haven’t created a directory by this name - this instruction
# creates a directory with this name if it doesn’t exist
WORKDIR /app
# Install any needed packages specified in requirements.txt
COPY . /app

ENV TWITTER_IP "0.0.0.0"
ENV TWITTER_PORT 9017

RUN pip install requests

# Run app.py when the container launches
# CMD python run.py
CMD ["python", "client.py"]