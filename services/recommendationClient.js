const net = require('net');

class RecommendationClient {
    constructor(host = 'cpp_server', port = 8080) {
        this.host = host;
        this.port = port;
        this.client = null;
    }

    async connect() {
        return new Promise((resolve, reject) => {
            this.client = new net.Socket();

            this.client.connect(this.port, this.host, () => {
                console.log('Connected to C++ server');
                resolve();
            });

            this.client.on('error', (err) => {
                console.error('Socket error:', err.message);
                reject(err);
            });
        });
    }

    async sendCommand(command) {
        if (!this.client || this.client.destroyed) {
            console.log('Recreating socket connection...');
            await this.connect();
        }
    
        return new Promise((resolve, reject) => {
            console.log(`Sending raw command to C++ server: "${command}"`);
            if (!this.client) {
                reject({ status: 500, error: 'Not connected to server' });
                return;
            }
    
            let response = '';
    
            const responseHandler = (data) => {
                response += data.toString();
                console.log(`Received raw response: "${response}"`);
    
                // Check for complete HTTP response (headers and body separated by \r\n\r\n)
                if (response.includes('\r\n\r\n')) {
                    this.client.removeListener('data', responseHandler);
    
                    // Split response into headers and body
                    const [headerSection, body] = response.split('\r\n\r\n');
                    const headers = headerSection.split('\r\n');
                    const statusLine = headers.shift();
    
                    // Extract HTTP status code from the status line
                    const match = statusLine.match(/^HTTP\/\d\.\d (\d{3})/);
                    if (!match) {
                        console.error('Failed to parse HTTP status line:', statusLine);
                        reject({ status: 500, error: 'Invalid Response', message: response });
                        return;
                    }
    
                    const statusCode = parseInt(match[1], 10);
                    console.log(`Parsed HTTP status code: ${statusCode}`);
    
                    // Handle different status codes
                    if (statusCode === 201) {
                        console.log('Successfully processed POST request');
                        resolve({ status: 201, message: body.trim() });
                    } else if (statusCode === 204) {
                        console.log('Successfully processed PATCH request');
                        resolve({ status: 204, message: 'No Content' });
                    } else if (statusCode === 200) {
                        console.log('Successfully processed GET request');
                        resolve({ status: 200, message: 'Ok' });
                    } else if (statusCode === 400) {
                        console.error(`C++ server responded with error: ${body.trim()}`);
                        reject({ status: 400, error: 'Bad Request', message: body.trim() });
                    } else if (statusCode === 500) {
                        console.error(`C++ server responded with internal error: ${body.trim()}`);
                        reject({ status: 500, error: 'Internal Server Error', message: body.trim() });
                    } else {
                        console.error('Unexpected status code:', statusCode);
                        reject({ status: 500, error: 'Unexpected Status Code', message: response });
                    }
                }
            };
    
            this.client.on('data', responseHandler);
            this.client.write(command + '\n'); // Send command with newline
        });
    }
    
    

    close() {
        if (this.client) {
            this.client.destroy();
            console.log('Disconnected from C++ server');
        }
    }
}

module.exports = RecommendationClient;