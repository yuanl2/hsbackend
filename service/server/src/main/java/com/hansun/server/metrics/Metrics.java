package com.hansun.server.metrics;

@SuppressWarnings("checkstyle:LineLength")
public class Metrics {
    // Metrics name
    public static final String DB_GET_PENDING_EXECUTIONS_BY_REGION_AND_TIMESLICE = "cassandra.getpendingexecutionsbyregionandtimeslice";
    public static final String DB_GET_RESULTS_BY_TASK_ID = "cassandra.getresultsbytaskid";
    public static final String DB_INSERT_RESULT = "cassandra.insertresult";
    public static final String DB_REMOVE_ALL_RESULTS_BY_TASK_ID = "cassandra.removeallresultsbytaskid";
    public static final String DB_REMOVE_SINGLE_RESULT_BY_TASK_ID_AND_DATE = "cassandra.removesingleresultbytaskidanddate";
    public static final String DB_GET_PENDING_EXECUTION = "cassandra.getpendingexecution";
    public static final String DB_INSERT_NEXT_TASK_TIME = "cassandra.insertnexttasktime";
    public static final String DB_DELETE_NEXT_TASK_TIME = "cassandra.deletenexttasktime";
    public static final String DB_GET_WRITETIME_NEXT_TASK_TIME = "cassandra.getwritetimenexttasktime";
    public static final String DB_INSERT_HEARTBEAT = "cassandra.insertheartbeat";
    public static final String DB_GET_HEARTBEATS = "cassandra.getheartbeats";
    public static final String DB_DELETE_HEARTBEAT = "cassandra.deleteheartbeat";
    public static final String DB_GET_SINGLE_HEARTBEAT = "cassandra.getsingleheartbeat";
    public static final String DB_DELETE_HEARTBEAT_V3 = "cassandra.deleteheartbeat.v3";
    public static final String DB_GET_HEARTBEATS_V3 = "cassandra.getheartbeats.v3";
    public static final String DB_GET_SINGLE_HEARTBEAT_V3 = "cassandra.getsingleheartbeat.v3";
    public static final String DB_INSERT_HEARTBEAT_V3 = "cassandra.insertheartbeat.v3";
    public static final String DB_GET_TASK_BY_ID = "cassandra.gettaskbyid";
    public static final String DB_GET_LAST_SWEEP = "cassandra.getlastsweep";
    public static final String DB_SET_LAST_SWEEP = "cassandra.setlastsweep";
    public static final String DB_GET_RETIRED_VERSION = "cassandra.getretiredversion";
    public static final String DB_SET_RETIRED_VERSION = "cassandra.setretiredversion";
    public static final String DB_GET_LAST_SEEN_RETIRED_INSTANT = "cassandra.getlastseenretiredinstant";
    public static final String DB_SET_LAST_SEEN_RETIRED_INSTANT = "cassandra.setlastseenretiredinstant";
    public static final String DB_GET_GLOBAL_MASTERS = "cassandra.getglobalmasters";
    public static final String DB_SET_GLOBAL_MASTERS = "cassandra.setglobalmasters";
    public static final String DB_GET_WRITETIME_FOR_GLOBAL_MASTERS = "cassandra.getwritetimeforglobalmasters";
    public static final String DB_GET_LAST_HEARTBEAT_MIGRATION = "cassandra.getlastheartbeatmigration";
    public static final String DB_SET_LAST_HEARTBEAT_MIGRATION = "cassandra.setlastheartbeatmigration";
    public static final String DB_INSERT_TASK = "cassandra.inserttask";
    public static final String DB_UPDATE_TASK = "cassandra.updatetask";
    public static final String DB_UPDATE_TASK_ACTION = "cassandra.updatetaskaction";
    public static final String DB_REMOVE_TASK = "cassandra.removetask";
    public static final String DB_GET_WRITETIME_TASK = "cassandra.getwritetimetask";
    public static final String DB_ERROR = "cassandra.error";
    public static final String DISPATCHER_TASKS_CIRCUIT_BREAKER = "dispatcher.tasks.circuitbreaker";
    public static final String DISPATCHER_TASKS_DISABLED = "dispatcher.tasks.disabled";
    public static final String DISPATCHER_TASKS_ERROR_ACTION = "dispatcher.tasks.erroraction";
    public static final String DISPATCHER_TASKS_EXECUTED = "dispatcher.tasks.executed";
    public static final String DISPATCHER_TASKS_EXECUTED_TIME = "dispatcher.tasks.executedtime";
    public static final String DISPATCHER_TASKS_FAILURE = "dispatcher.tasks.failure";
    public static final String DISPATCHER_TASKS_RETRY = "dispatcher.tasks.retry";
    public static final String DISPATCHER_TASKS_SUCCESS = "dispatcher.tasks.success";
    public static final String DISPATCHER_TASKS_DELAYED = "dispatcher.tasks.delayed";
    public static final String DISPATCHER_TASKS_DELAY = "dispatcher.tasks.delay";
    public static final String DISPATCHER_TASKS_SKIPPED = "dispatcher.tasks.skipped";
    public static final String DISPATCHER_TASKS_SWEEP_TASKS = "dispatcher.tasks.sweep.tasks";
    public static final String DISPATCHER_TASKS_SWEEP_HEARTBEATS = "dispatcher.tasks.sweep.heartbeats";
    public static final String DISPATCHER_TASKS_SWEPT_RECORD_PER_TIMESLICE = "dispatcher.tasks.sweptrecordpertimeslice";
    public static final String DISPATCHER_TASKS_SWEPT_RETIRED_INSTANT_RECORD_PER_TIMESLICE = "dispatcher.tasks.sweptretiredinstantrecordpertimeslice";
    public static final String DISPATCHER_TASKS_MIGRATE_HEARTBEATS = "dispatcher.tasks.migrate.heartbeats";
    public static final String DISPATCHER_EXECUTOR_REJECT = "dispatcher.executor.reject";
    public static final String DISPATCHER_CIRCUIT_BREAKER = "dispatcher.circuitbreaker";
    public static final String REDIS_OPERATION_DELETE = "redis.operation.delete";
    public static final String REDIS_OPERATION_WRITE = "redis.operation.write";
    public static final String REDIS_OPERATION_READ = "redis.operation.read";
    public static final String REDIS_TASK_CACHE_SIZE = "redis.task.cache.size";
    public static final String REDIS_TASK_CACHE_INSERT = "redis.task.cache.insert";
    public static final String REDIS_TASK_CACHE_GET = "redis.task.cache.get";
    public static final String REDIS_TASK_CACHE_HIT = "redis.task.cache.hit";
    public static final String REDIS_TASK_CACHE_MISS = "redis.task.cache.miss";
    public static final String REDIS_OPERATION_GET_POOL_RESOURCE = "redis.operation.getresource";
    public static final String REDIS_COLLECTIONS_HEARTBEAT_TASK_BUCKET_OPERATION_READ = "redis.collections.heartbeattaskbucket.read";
    public static final String REDIS_COLLECTIONS_QUEUED_TASK_BUCKET_OPERATION_READ = "redis.collections.queuedtaskbucket.read";
    public static final String REDIS_COLLECTIONS_HEARTBEAT_TASK_BUCKET_OPERATION_DELETE = "redis.collections.heartbeattaskbucket.delete";
    public static final String REDIS_COLLECTIONS_QUEUED_TASK_BUCKET_OPERATION_DELETE = "redis.collections.queuedtaskbucket.delete";
    public static final String REDIS_ERROR = "redis.error";
    public static final String RABBITMQ_PUBLISH = "queues.rabbitmq.publish";
    public static final String RABBITMQ_SCHEDULER_NOW_MESSAGE_SIZE = "queues.rabbitmq.schedulernow.size";
    public static final String RABBITMQ_UNKNOWN_MESSAGE = "queues.rabbitmq.unknown.message";
    public static final String MACHINE_ACCOUNT_ALREADY_EXPIRED = "machine.account.already.expired";
    public static final String MACHINE_ACCOUNT_WILL_EXPIRE = "machine.account.will.expire";
    public static final String COUNTERPOLICY_TOO_MANY_REQUEST = "counterpolicy.too.many.request";

    // Metrics tag
    public static final String TAG_CLIENT_ID = "clientid";

    // Metrics field
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_GAUGE = "gauge";
    public static final String FIELD_MESSAGE = "message";
}
