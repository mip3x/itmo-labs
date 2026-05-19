FROM postgres:latest

COPY conf/* /etc/postgresql/
COPY scripts/* /home/scripts/
COPY init/init-standby.sh /docker-entrypoint-initdb.d/init-standby.sh
RUN chmod +x /home/scripts/read_client.sh
RUN chmod +x /home/scripts/request-rejoin-as-standby.sh
RUN chmod +x /home/scripts/lab-entrypoint.sh
RUN chmod +x /docker-entrypoint-initdb.d/init-standby.sh
ENTRYPOINT ["/home/scripts/lab-entrypoint.sh"]
CMD ["postgres"]
