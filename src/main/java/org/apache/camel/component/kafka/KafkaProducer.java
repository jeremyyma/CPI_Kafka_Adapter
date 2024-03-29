/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.kafka;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelException;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultAsyncProducer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

public class KafkaProducer extends DefaultAsyncProducer {
    
    private org.apache.kafka.clients.producer.KafkaProducer kafkaProducer;
    private final KafkaEndpoint endpoint;
    private ExecutorService workerPool;
    private boolean shutdownWorkerPool;

    public KafkaProducer(KafkaEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }
    
    Properties getProps() {
        //Properties props = endpoint.getConfiguration().createProducerProperties();
        Properties props =new Properties();
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, endpoint.getConfiguration().getSecurityProtocol());
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, endpoint.getConfiguration().getSslEndpointAlgorithm());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, endpoint.getConfiguration().getSerializerClass());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, endpoint.getConfiguration().getSerializerClass());
        props.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
        //props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"J4ODU4BPEWBCQKZU\" password=\"xxxxx\";");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        addPropertyIfNotNull(props, ProducerConfig.MAX_REQUEST_SIZE_CONFIG, endpoint.getConfiguration().getMaxRequestSize());
        addPropertyIfNotNull(props, ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, endpoint.getConfiguration().getRequestTimeoutMs()); 
       
        /**addPropertyIfNotNull(props, ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, endpoint.getConfiguration().getKeySerializer());
        addPropertyIfNotNull(props, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, endpoint.getConfiguration().getValueSerializer());
        addPropertyIfNotNull(props, ProducerConfig.ACKS_CONFIG, endpoint.getConfiguration().getRequestRequiredAcks());
        addPropertyIfNotNull(props, ProducerConfig.BUFFER_MEMORY_CONFIG, endpoint.getConfiguration().getBufferMemorySize());
        addPropertyIfNotNull(props, ProducerConfig.COMPRESSION_TYPE_CONFIG, endpoint.getConfiguration().getCompressionCodec());
        addPropertyIfNotNull(props, ProducerConfig.RETRIES_CONFIG, endpoint.getConfiguration().getRetries());
        addPropertyIfNotNull(props, ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, endpoint.getConfiguration().getInterceptorClasses());
        addPropertyIfNotNull(props, ProducerConfig.BATCH_SIZE_CONFIG, endpoint.getConfiguration().getProducerBatchSize());
        addPropertyIfNotNull(props, ProducerConfig.CLIENT_ID_CONFIG, endpoint.getConfiguration().getClientId());
        addPropertyIfNotNull(props, ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, endpoint.getConfiguration().getConnectionMaxIdleMs());
        addPropertyIfNotNull(props, ProducerConfig.LINGER_MS_CONFIG, endpoint.getConfiguration().getLingerMs());
        addPropertyIfNotNull(props, ProducerConfig.MAX_BLOCK_MS_CONFIG, endpoint.getConfiguration().getMaxBlockMs());
        addPropertyIfNotNull(props, ProducerConfig.MAX_REQUEST_SIZE_CONFIG, endpoint.getConfiguration().getMaxRequestSize());
        addPropertyIfNotNull(props, ProducerConfig.PARTITIONER_CLASS_CONFIG, endpoint.getConfiguration().getPartitioner());
        addPropertyIfNotNull(props, ProducerConfig.RECEIVE_BUFFER_CONFIG, endpoint.getConfiguration().getReceiveBufferBytes());
        addPropertyIfNotNull(props, ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, endpoint.getConfiguration().getRequestTimeoutMs());
        addPropertyIfNotNull(props, ProducerConfig.SEND_BUFFER_CONFIG, endpoint.getConfiguration().getSendBufferBytes());
        addPropertyIfNotNull(props, ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, endpoint.getConfiguration().getMaxInFlightRequest());
        addPropertyIfNotNull(props, ProducerConfig.METADATA_MAX_AGE_CONFIG, endpoint.getConfiguration().getMetadataMaxAgeMs());
        addPropertyIfNotNull(props, ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG, endpoint.getConfiguration().getMetricReporters());
        addPropertyIfNotNull(props, ProducerConfig.METRICS_NUM_SAMPLES_CONFIG, endpoint.getConfiguration().getNoOfMetricsSample());
        addPropertyIfNotNull(props, ProducerConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG, endpoint.getConfiguration().getMetricsSampleWindowMs());
        addPropertyIfNotNull(props, ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, endpoint.getConfiguration().getReconnectBackoffMs());
        addPropertyIfNotNull(props, ProducerConfig.RETRY_BACKOFF_MS_CONFIG, endpoint.getConfiguration().getRetryBackoffMs());
        //addPropertyIfNotNull(props, ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, isEnableIdempotence());
        addPropertyIfNotNull(props, ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, endpoint.getConfiguration().getReconnectBackoffMaxMs());
        **/
        endpoint.updateClassProperties(props);
        if (endpoint.getBrokers() != null) {
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint.getBrokers());
        }
        return props;
    }

    private static <T> void addPropertyIfNotNull(Properties props, String key, T value) {
        if (value != null) {
            // Kafka expects all properties as String
            props.put(key, value.toString());
        }
    }
    public org.apache.kafka.clients.producer.KafkaProducer getKafkaProducer() {
        return kafkaProducer;
    }

    /**
     * To use a custom {@link org.apache.kafka.clients.producer.KafkaProducer} instance.
     */
    public void setKafkaProducer(org.apache.kafka.clients.producer.KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public ExecutorService getWorkerPool() {
        return workerPool;
    }

    public void setWorkerPool(ExecutorService workerPool) {
        this.workerPool = workerPool;
    }

    @Override
    protected void doStart() throws Exception {
        Properties props = getProps();
        
        if (kafkaProducer == null) {
            ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                //Fix for running camel-kafka in OSGI see KAFKA-3218
                //Thread.currentThread().setContextClassLoader(null);
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                kafkaProducer = new org.apache.kafka.clients.producer.KafkaProducer(props);
            } finally {
                Thread.currentThread().setContextClassLoader(threadClassLoader);
            }
        }

        // if we are in asynchronous mode we need a worker pool
        if (!endpoint.isSynchronous() && workerPool == null) {
            workerPool = endpoint.createProducerExecutor();
            // we create a thread pool so we should also shut it down
            shutdownWorkerPool = true;
        }
    }

    @Override
    protected void doStop() throws Exception {
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }

        if (shutdownWorkerPool && workerPool != null) {
            endpoint.getCamelContext().getExecutorServiceManager().shutdown(workerPool);
            workerPool = null;
        }
    }

    @SuppressWarnings("unchecked")
    protected Iterator<ProducerRecord> createRecorder(Exchange exchange) throws CamelException {
        String topic = endpoint.getTopic();
        if (!endpoint.isBridgeEndpoint()) {
            topic = exchange.getIn().getHeader(KafkaConstants.TOPIC, topic, String.class);
        }
        if (topic == null) {
            throw new CamelExchangeException("No topic key set", exchange);
        }
        final Object partitionKey = exchange.getIn().getHeader(KafkaConstants.PARTITION_KEY);
        final boolean hasPartitionKey = partitionKey != null;

        final Object messageKey = exchange.getIn().getHeader(KafkaConstants.KEY);
        final boolean hasMessageKey = messageKey != null;

        Object msg = exchange.getIn().getBody();
        Iterator<Object> iterator = null;
        if (msg instanceof Iterable) {
            iterator = ((Iterable<Object>)msg).iterator();
        } else if (msg instanceof Iterator) {
            iterator = (Iterator<Object>)msg;
        }
        if (iterator != null) {
            final Iterator<Object> msgList = iterator;
            final String msgTopic = topic;
            return new Iterator<ProducerRecord>() {
                @Override
                public boolean hasNext() {
                    return msgList.hasNext();
                }

                @Override
                public ProducerRecord next() {
                    if (hasPartitionKey && hasMessageKey) {
                        return new ProducerRecord(msgTopic, new Integer(partitionKey.toString()), messageKey, msgList.next());
                    } else if (hasMessageKey) {
                        return new ProducerRecord(msgTopic, messageKey, msgList.next());
                    }
                    return new ProducerRecord(msgTopic, msgList.next());
                }

                @Override
                public void remove() {
                    msgList.remove();
                }
            };
        }
        ProducerRecord record;
        if (hasPartitionKey && hasMessageKey) {
            record = new ProducerRecord(topic, new Integer(partitionKey.toString()), messageKey, msg);
        } else if (hasMessageKey) {
            record = new ProducerRecord(topic, messageKey, msg);
        } else {
            log.warn("No message key or partition key set");
            record = new ProducerRecord(topic, msg);
        }
        return Collections.singletonList(record).iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    // Camel calls this method if the endpoint isSynchronous(), as the KafkaEndpoint creates a SynchronousDelegateProducer for it
    public void process(Exchange exchange) throws Exception {
        Iterator<ProducerRecord> c = createRecorder(exchange);
        List<Future<ProducerRecord>> futures = new LinkedList<Future<ProducerRecord>>();
        while (c.hasNext()) {
            futures.add(kafkaProducer.send(c.next()));
        }
        for (Future<ProducerRecord> f : futures) {
            //wait for them all to be sent
            f.get();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            Iterator<ProducerRecord> c = createRecorder(exchange);
            KafkaProducerCallBack cb = new KafkaProducerCallBack(exchange, callback);
            while (c.hasNext()) {
                cb.increment();
                kafkaProducer.send(c.next(), cb);
            }
            return cb.allSent();
        } catch (Exception ex) {
            exchange.setException(ex);
        }
        callback.done(true);
        return true;
    }

    private final class KafkaProducerCallBack implements Callback {

        private final Exchange exchange;
        private final AsyncCallback callback;
        private final AtomicInteger count = new AtomicInteger(1);

        KafkaProducerCallBack(Exchange exchange, AsyncCallback callback) {
            this.exchange = exchange;
            this.callback = callback;
        }

        void increment() {
            count.incrementAndGet();
        }
        boolean allSent() {
            if (count.decrementAndGet() == 0) {
                //was able to get all the work done while queuing the requests
                callback.done(true);
                return true;
            }
            return false;
        }
        
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                exchange.setException(e);
            }
            if (count.decrementAndGet() == 0) {
                // use worker pool to continue routing the exchange
                // as this thread is from Kafka Callback and should not be used by Camel routing
                workerPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        callback.done(false);
                    }
                });
            }
        }
    }

}
