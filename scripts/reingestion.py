import json
import time
from google.cloud import datastore, pubsub_v1

PROJECT_ID = "triangl-215714"
PUBSUB_TOPIC = "ingestion-prod"
FETCH_PAGE_SIZE = 1000

router_maps = {}
published_items_count = 0

def create_client(project_id):
    return datastore.Client(project_id)

def apply_paged_entities(client, kind, publisher, topic_path):
    entities = None
    cursor = None
    while cursor is not None or entities is None:
        entities, cursor = get_entities(client, kind, cursor)
        publish_pubsub_messages(entities, publisher, topic_path)

def get_entity(gcloud_entity):
    entity = {"data": gcloud_entity.copy()}
    entity["data"]["id"] = gcloud_entity.key.path[0]["name"]
    entity["kind"] = gcloud_entity.key.path[0]["kind"]
    return entity

def get_entities(client, kind, cursor=None, limit=FETCH_PAGE_SIZE):
    query = client.query(kind = kind)
    query_iter = query.fetch(start_cursor=cursor, limit=limit)
    gcloud_entities = list(query_iter)
    next_cursor = query_iter.next_page_token
    return list(map(get_entity, gcloud_entities)), next_cursor

def publish_pubsub_messages(entities, publisher, topic_path):
    global published_items_count
    for entity in entities:
        json_data = json.dumps([entity["data"]])

        if entity["kind"] == "Customer":
            publisher.publish(topic_path, json_data, operation = "APPLY_CUSTOMER")
        elif entity["kind"] == "TrackingPoint":
            router_id = entity["data"]["routerDataPoints"][0]["router"]["id"]
            additional = {"mapId": router_maps[router_id]}
            additional_json = json.dumps(additional)
            publisher.publish(topic_path, json_data, operation="APPLY_TRACKING_POINT", additional=additional_json)

        published_items_count += 1
        print("%s: %s %s" % (published_items_count, entity["kind"], entity["data"]["id"]))
        time.sleep(0.01)

def main():
    global router_maps

    client = create_client(PROJECT_ID)
    publisher = pubsub_v1.PublisherClient()
    topic_path = publisher.topic_path(PROJECT_ID, PUBSUB_TOPIC)

    customers, _ = get_entities(client, "Customer", None, None)
    publish_pubsub_messages(customers, publisher, topic_path)

    for customer in customers:
        map = customer["data"]["maps"][0]
        for router in map["router"]:
            router_maps[router["id"]] = map["id"]

    print(router_maps)

    apply_paged_entities(client, "TrackingPoint", publisher, topic_path)

if __name__ == "__main__":
    main()