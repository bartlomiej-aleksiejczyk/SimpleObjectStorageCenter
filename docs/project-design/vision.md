Write ordinary markdown
Inspire with hypermedia systems

- Write notes using markdown and local file system
- structure them using filesystem directory tree
- Enumerate notes using unique Id add other metadata
- embed hypermedia inside notes, just link to files from same direcotry
- link note with others using unique Id
- notes must be human readable and accessible without any software
--- 

Compile resulting markdown to hypermedia
-  check if IDs are unique
-  parse all markdown for files to upload
-  check if all files are on its place
-  check if all links are working and are pointing to existing 
-  map markdown files to url structure
-  upload files to S3 like storage single bucket with flat object structure
-  convert markdown to htlm templates 
    -  map file structure to url structure
    -  convert local file links to S3 links
    -  convert other documents links to urls
    -  convert markdown files to html
- make navigation elements that will present structure in convenient way
- serve everything as static pages in form of personal wiki

Precent file name conflicts 
- use absolute path hash
Find orphan files and remove

---

Further steps
- make search engine to index and perform search (can do ML things like vector search or other similar things)
-Connect hypermedia root with git repository
