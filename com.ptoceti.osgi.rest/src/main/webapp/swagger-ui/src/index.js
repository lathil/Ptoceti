import SwaggerUI from 'swagger-ui'
import 'swagger-ui/dist/swagger-ui.css';

//const spec = require('./swagger-config.yaml');
var openapipath = window.location.pathname;
openapipath = openapipath.replace('swagger-ui/index.html', 'openapi');
const ui = SwaggerUI({
    url: openapipath,
    dom_id: '#swagger'
});
