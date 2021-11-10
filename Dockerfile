FROM maven:3.8.2-jdk-11

#install both docker CLI and docker-compose
RUN apt-get update && apt install -y docker-compose

#upgrade docker-compose version
RUN curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
	chmod go+x /usr/local/bin/docker-compose

#allow image to access home directory when not running as root
RUN chmod 777 /root

#make ssh happy
RUN groupadd -g 1000 jenkins
RUN useradd -u 1000 -g 1000 jenkins -d /root
