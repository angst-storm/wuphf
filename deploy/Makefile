UMS_IMAGE ?= wuphf-ums:latest
MESSAGES_IMAGE ?= wuphf-messages:latest
CLUSTER ?= wuphf

.PHONY: kind-cluster delete-kind-cluster images image-ums image-messages postgres-db deploy

kind-cluster:
	kind create cluster --name $(CLUSTER) --config kind-config.yaml
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
	kubectl wait --namespace ingress-nginx \
		--for=condition=ready pod \
		--selector=app.kubernetes.io/component=controller \
		--timeout=90s

delete-kind-cluster:
	kind delete cluster -n $(CLUSTER)

postgres-db:
	kubectl create namespace postgres
	kubectl apply -k ../db/init
	helm install postgres oci://registry-1.docker.io/bitnamicharts/postgresql \
		--namespace postgres --values postgres/values.yaml

image-ums:
	docker build -f docker/Dockerfile --build-arg MODULE=ums -t $(UMS_IMAGE) ..
	kind load docker-image $(UMS_IMAGE) --name $(CLUSTER)

image-messages:
	docker build -f docker/Dockerfile --build-arg MODULE=messages -t $(MESSAGES_IMAGE) ..
	kind load docker-image $(MESSAGES_IMAGE) --name $(CLUSTER)

images: image-ums image-messages

deploy:
	kubectl apply -k .
