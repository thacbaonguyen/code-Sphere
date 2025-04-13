### `Build codesphere to production`
#### `step 1:` Clone source code
* Clone codesphere and codesphere-fe into the project's shared directory.  
FE: [Link source code](https://github.com/thacbaonguyen/codeSphere-Fe)  
BE: [Link source code](https://github.com/thacbaonguyen/code-Sphere)
- project structure
```text
my-project
├── codeSphere-Fe
│   └── codesphere
│       ├── Dockerfile
│       ├── README.md
│       ├── angular.json
│       ├── dump.rdb
│       ├── karma.conf.js
│       ├── nginx.conf
│       ├── package-lock.json
│       ├── package.json
│       ├── src/
│       ├── tsconfig.app.json
│       ├── tsconfig.json
│       └── tsconfig.spec.json
└── code-Sphere
    └── codeSphere
        └── codeSphere
            ├── Dockerfile
            ├── codephere.sql
            ├── codesphere.sql
            ├── docker-compose.yml
            ├── mvnw
            ├── mvnw.cmd
            ├── pom.xml
            └── src/
```

#### `step 2:` Install tools 
* Install Docker, Nginx
1. Install
```text
sudo apt-get update
sudo apt-get install docker-compose-plugin
docker compose version
```
```text
sudo apt install nginx
```
2. Start services

```text
systemctl enable docker
systemctl start docker
systemctl start nginx
```
3. Check service 
```text
systemctl status docker
systemctl status nginx
sudo apt install net-tools
netnstat -tlpun
```


#### `step 3:`  Config application.yml
* Configure the variables in the application.yml file for your Spring Boot application.  

#### `step 4:`  Build and up container
* Execute the following commands.

```text
docker compose build
docker compose up -d
```
* Check the status.

```text
docker compose ps
```
#### `step 5:`  Config ssl cert
* Using open ssl or certbot (If you have domain)
OpenSSL
```text
sudo apt instal openssl
sudo openssl x509 -req -days 365 -in /etc/nginx/ssl/nginx-selfsigned.csr -signkey /etc/nginx/ssl/nginx-selfsigned.key -out /etc/nginx/ssl/nginx-selfsigned.crt
```
Certbot
```text
sudo certbot --nginx -d your_domain
```
#### `step 6:` Config nginx reverse proxy
* Configure Nginx as a reverse proxy.
```text
cd /etc/nginx/conf.d/
nano (vi) codesphere.conf
```
in codesphere.conf
```text
# HTTP server block - redirect to HTTPS
server {
    server_name your_domain;

    listen [::]:443 ssl ipv6only=on; 
    listen 443 ssl; # managed by Certbot
    ssl_certificate /etc/your_ssl_cert; 
    ssl_certificate_key /etc/your_ssl_key;
    #include /etc/letsencrypt/options-ssl-nginx.conf; 
    #ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; 

    location / {
        proxy_pass http://localhost:4200;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
server {
    if ($host = your_domain) {
        return 301 https://$host$request_uri;
    } # managed by Certbot
    listen 80;
    listen [::]:80;
    server_name your_domain;
    return 404;
}

```
* Reload
```text
sudo nginx -t
systemctl restart nginx
```  
If the containers show the status as "up," then this step is complete.   
#### `-> Now you can access your project directly on the internet.`
### `Entity Relationship Model`

![eer](https://github.com/thacbaonguyen/codeSphere_repo/blob/master/overview-system/erd-diagram.png)

### `Enhanced Entity-Relationship Model`
#### `Some entities will be added during the application development process.`
![erd](https://github.com/thacbaonguyen/code-Sphere/blob/master/overview-system/err-diagram-update.png)



