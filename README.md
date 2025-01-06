# SiteSnap-WebRenderWorkerService


# System Architecture for Website Caching and SEO Optimization

## Purpose

The primary purpose of this system is to cache websites efficiently and serve the cached HTML to web crawlers (bots) to reduce the time required to retrieve the content. This enhances the overall performance of the website in search engine crawling, thereby improving its SEO ranking. By delivering pre-cached HTML content to crawlers, the system minimizes server load, accelerates response times, and ensures a seamless experience for both search engines and end-users.

### Key Benefits:
1. **Improved SEO Performance:** Faster content delivery to search engine crawlers improves the crawling and indexing efficiency, leading to higher search engine rankings.
2. **Reduced Latency:** Serving cached content significantly reduces the time bots spend waiting for HTML responses.
3. **Optimized Resource Usage:** By leveraging caching mechanisms, server resources are utilized efficiently, reducing overhead and ensuring scalability.

---
![System_Archi](https://github.com/panditaditya0/SiteSnap-WebRenderWorkerService/blob/main/systemArchi.png)






## Architecture Overview

### Components:
1. **Website:**
    - Acts as the entry point for bots and end-users.
    - Redirects traffic to the `FetchCachedWebsiteApp` for optimized performance.

2. **FetchCachedWebsiteApp:**
    - Contains the following subcomponents:
        - **Controller:** Handles incoming requests and directs them to the business logic layer.
        - **Business Layer:** Core logic for fetching and serving cached HTML content.
        - **Log Management:** Maintains logs for request processing and caching activities.

3. **Cache Database (Cache DB):**
    - Stores pre-rendered HTML content for fast retrieval by the `FetchCachedWebsiteApp`.

4. **SiteMap Crawler App:**
    - Allows clients to submit demand for specific pages to be crawled and cached.
    - Pushes crawl requests to the `Msg Broker`.

5. **Message Broker (Msg Broker):**
    - Acts as a queue for handling crawl requests.
    - Distributes tasks to scalable worker nodes.

6. **Scalable Workers:**
    - Use WebRender and WebDriver Server components to process crawl requests.
    - Dump logs for monitoring and debugging purposes.

7. **SQL Database:**
    - Stores metadata and crawl request details for efficient management and traceability.

8. **Management Service:**
    - Includes an **OnExpire** module to handle cache expiration.
    - Auto-scales worker nodes as per system demand.

9. **Monitoring and Logging Systems:**
    - Integrated dashboards for real-time monitoring and log management:
