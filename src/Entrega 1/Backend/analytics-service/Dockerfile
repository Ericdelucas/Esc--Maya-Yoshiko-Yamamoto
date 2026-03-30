FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    ca-certificates \
  && update-ca-certificates \
  && rm -rf /var/lib/apt/lists/*

COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir \
  --trusted-host pypi.org \
  --trusted-host files.pythonhosted.org \
  -r /app/requirements.txt

COPY . /app

EXPOSE 8050

CMD ["python", "-m", "uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8050"]
