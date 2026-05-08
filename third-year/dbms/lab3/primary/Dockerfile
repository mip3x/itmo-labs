FROM postgres:latest

COPY conf/* /etc/postgresql/
COPY scripts/* /home/scripts/
COPY init/init-primary.sh /docker-entrypoint-initdb.d/init-primary.sh
RUN chmod +x /home/scripts/read_client.sh
RUN chmod +x /home/scripts/write_client.sh
RUN chmod +x /docker-entrypoint-initdb.d/init-primary.sh
