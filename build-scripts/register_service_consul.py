import consul
import time

print("api-server service registration script")
c = consul.Consul()

c.connect("localhost", 8500, 'http', True)


# while True:
#     time.sleep(1)
c.agent.service.register("API-Server", tags=["urlprefix-API-Server/"], check=consul.Check.tcp("localhost", 9190, 1))
    # print("api-server service registered into consul")

print("api-server service Exit")
