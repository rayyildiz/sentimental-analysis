build:
	sbt clean assembly
	docker build -t rayyildiz/sentiment_analyzer .
	docker push rayyildiz/sentiment_analyzer

run:
	docker run -d -p 8080:8080 rayyildiz/sentiment_analyzer

deploy: build
	docker push rayyildiz/sentiment_analyzer

k8s: deploy

	kubectl apply -f sentiment.yaml
