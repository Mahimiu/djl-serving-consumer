# cd djl-serving
# docker build -t mosazhaw/djl-serving:latest .
# docker push mosazhaw/djl-serving:latest

FROM deepjavalibrary/djl-serving:0.36.0

# Copy Model
COPY *.zip /opt/ml/
RUN unzip /opt/ml/traced_resnet18.zip -d /opt/ml/model/traced_resnet18
RUN rm /opt/ml/traced_resnet18.zip
