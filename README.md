# teachme-course-service

TeachMe actúa como plataforma de enseñanza mediante cursos para estudiantes interesados. Los usuarios son personas que buscan realizar cursos, dichos cursos pueden ser cerficados profesionales que promuevan la inserción laboral.
Microservicio de cursos

## Development Setup

### Prerequisites

Ensure you have the following installed:

- **Java 17 or obove**
- **A database in MongoDB**

### Starting the Development Environment

1. Clone the repository
2. Configure the connection:
    Add the MongoDB URI to 'application.properties' under the 'MONGODB_URI' property.
    Add the Youtube API KEY to 'application.properties' under the 'YOUTUBE_API_KEY' property.
    Add the Base URL to 'application.properties' under the 'BASE_URL' property.
    Add the FORUM CREATED URL to 'application.properties' under the 'FORUM_CREATED_URL' 
    property.
    Add the FORUM DELETE URL to 'application.properties' under the 'FORUM_DELETE_URL' property.
 **Important:** These keys are available in the `ci.yaml` file in /.github

### Running the Service
```bash
mvn spring-boot:run 
```
