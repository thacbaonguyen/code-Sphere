# CI/CD guide
## `Requirements`
- host your gitlab server
- host your harbor registry
## `project structure`
FE: [Link source code](https://github.com/thacbaonguyen/codeSphere-Fe)  
BE: [Link source code](https://github.com/thacbaonguyen/code-Sphere)
- Project structure
```text
my-project/
   ├── .gitlab-ci.yml
   ├── README.md
   ├── be/
   │   ├── codesphere.sql
   │   ├── docker-compose.yml
   │   ├── Dockerfile
   │   ├── mvnw
   │   ├── mvnw.cmd
   │   ├── pom.xml
   │   ├── src/
   └── fe/
       ├── angular.json
       ├── Dockerfile
       ├── karma.conf.js
       ├── nginx.conf
       ├── package.json
       ├── src/

```
