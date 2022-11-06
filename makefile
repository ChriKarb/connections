pave_env:
	brew install docker
	brew install minikube
	brew install openjdk
	brew install helm

minikube_start:
	minikube status || (minikube start; exit 1)

minikube_stop:
	minikube stop

build_docker_image:
	source current_builder.sh; docker build -t 2e2connections:latest .

build_minikube_docker_image_mvn:
    #https://medium.com/swlh/build-a-docker-image-using-maven-and-spring-boot-58147045a400
    #use defined dockerd daemon to use the minkube registry
	source current_builder.sh ; ./mvnw -X spring-boot:build-image -Dspring-boot.build-image.imageName=experimentrepo/connections

build_local:
	./mvnw package

run_local:
	java -jar ./target/connections-0.0.1-SNAPSHOT.jar

deploy_connections_in_minikube:
	helm install --set image.repository=2e2connections connectionse2e  ./connectionse2e

deploy_buildpack_image_connections_in_minikube:
	helm install --set image.repository=experimentrepo/connections connectionse2e  ./connectionse2e

update_connections_in_minikube:
	helm upgrade connectionse2e ./connectionse2e

uninstall_connections_in_minikube:
	helm uninstall connectionse2e

show_local_docker_images:
	docker images

show_minikube_docker_images:
	source current_builder.sh ; docker images

get_minikube_dockerd_setting_for_local_builds:
	minikube -p minikube docker-env > current_builder.sh

test_local_run:
	curl http://localhost:8080/actuator/health


install_prometheus:
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm install my-prometheus prometheus-community/prometheus --version 15.16.1\

build_all: minikube_start get_minikube_dockerd_setting_for_local_builds build_image_mvn