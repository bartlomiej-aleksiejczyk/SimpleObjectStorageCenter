# SimpleObjectStorageCenter functional roadmap

## 1.0.0 Functional Specification

1. **System Overview**
   - Directory will be treated as a namespace for an object storage.
   - Will treat every upload as an object.
     - Could hold metadata for each file.
     - Lack of metadata scenario must be taken into account.
     - Metadata must be purged before sending to the end-user.

2. **Core Functionalities**
   - Handle file upload and download for a given directory.
   - Directory can be `PUBLIC` or `PRIVATE`.

3. **Authorization and Access Control**
   - Will authorize every request based on directory access configuration.
   - To access a given directory, a client should have one of the base roles:
     - `VIEWER` for read-only interactions.
     - `EDITOR` for all possible interactions with directory content.
     - `OWNER` for altering directory settings.
   - Clients can have a global role for system-wide interactions, such as:
     - `ADMINISTRATOR` for all actions with users and meta-actions with directories, i.e., directory creation and removal.
     - `INSPECTOR` for read-only actions such as reading logs and downloading all files.
     - `MODERATOR` for actions tied to users.
     - `SUPER_EDITOR` can create directories.

4. **Authentication**
   - Use API keys based, where each client has their key.
   - Implement basic key retention and generation.
   - Use a better method than basic auth with API keys in request headers.

5. **Technical Requirements and Data Management**
    - Must use some form of DB for configuration management.
        - Even in the form of XML.
        - If the database is file-based, must prepare for multi-write scenarios.
    - Must log every action, soft delete is crucial then
    - Must prevent logs from filling all storage
    - Must implement OpenAPI solution

6. **Networking and Request Handling**
   - Will accept forwarded requests and respond to the original sender.

---

## 2.0.0 Functional Specification
1. **Core Functionalities**
    * Client for Google Drive Experience:
        * The system will provide a client interface or integration mimicking the Google Drive experience, focusing on user-friendly file management and interactions.
2. **Technical Requirements and Data Management**
    * HTTPS full support :D
    * Cache Management:
        * Implement a comprehensive cache management system to enhance performance and reduce latency.
        * The system should automatically handle cache invalidation and updates based on file changes or at specified intervals.

## Undefined Release Functional Specification
* Video streaming