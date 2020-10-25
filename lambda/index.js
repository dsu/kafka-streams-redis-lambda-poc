var redis = require('redis');
var client = redis.createClient({
    host: process.env.REDIS_HOST || '127.0.0.1'
});

var KafkaRest = require('kafka-rest');
var kafka = new KafkaRest({
    'url': 'http://kafka-rest-proxy:8082'
});
var topic = "products"

exports.handler = async (event, context, cb) => {

    console.log("DEBUG: Starting event processing...");

    var key = event.key;

    if (event.consume) {
        const promise = new Promise(function(resolve, reject) {

            var consumerGroup = "my-consumer-group-" + new Date().getTime()

            kafka.consumer(consumerGroup).join({
                "format": "binary",
                "auto.commit.enable": "true",
                "auto.offset.reset": "smallest",
            }, function(err, consumer_instance) {
                if (err) {
                    console.error(err);
                    resolve(formatResponse(err, 500));
                } else {
                    console.log("Joined the consumer group " + consumerGroup)
                    var stream = consumer_instance.subscribe(topic)
                    stream.on('data', function(msgs) {
                        for (var i = 0; i < msgs.length; i++) {
                            console.log("Got a message: key=" + msgs[i].key + " partition=" + msgs[i].partition);

                            console.log('Decoded value:')
                            console.log(msgs[i].value.toString('utf8'));

                            if (msgs[i].key == key) {
                                resolve(formatStringResponse(msgs[i].value.toString('utf8'), 200));
                                return;
                            }
                        }
                    });

                    stream.on('error', function(err) {
                        console.log("Something broke: " + err);
                        resolve(formatResponse(err, 500));
                        consumer_instance.shutdown(function() {
                            console.log("Shutdown complete.");
                        });
                    });

                }
            });
        })
        return promise
    }

    if (event.get) {
        const promise = new Promise(function(resolve, reject) {

            let key = event.key;

            client.on('connect', function() {
                console.log('connected');
            })

            console.log('Get the  product ' + key)
            client.hgetall(key, function(err, results) {
                if (err) {
                    console.error(err)
                    resolve(formatResponse(err, 400));
                } else {
                    // do something with results
                    console.log("Result:")
                    console.log(results);
                    if (!results || results === null || results === 'null') {
                        console.log('Empty result')
                        resolve(formatResponse({}, 204));
                    } else {
                        resolve(formatResponse(results, 200));
                    }
                }
            });

        })
        return promise

    } else {
        return formatResponse({
            error: 'Unknown event'
        }, 400);
    }
}

function debug(context, event) {
    console.log(process.execPath)
    console.log(process.execArgv)
    console.log(process.argv)
    console.log(process.cwd())
    console.log(process.mainModule.filename)
    console.log(__filename)
    console.log(process.env)
    console.log(process.getuid())
    console.log(process.getgid())
    console.log(process.geteuid())
    console.log(process.getegid())
    console.log(process.getgroups())
    console.log(process.umask())
    console.log(event)
    console.log(context)
}

function formatResponse(data, code) {
    return {
        statusCode: code,
        headers: {
            'Access-Control-Allow-Origin': '*'
        },
        body: JSON.stringify(data)
    }
}

function formatStringResponse(data, code) {
    return {
        statusCode: code,
        headers: {
            'Access-Control-Allow-Origin': '*'
        },
        body: data
    }
}