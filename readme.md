# Bookmark Generator
Converts an yaml file with bookmark data to a a bookmark html file that can be imported in Firefox, Chrome and Internet explorer.

Supports name, url, description, tags and icons.

Comes in both a python and a groovy version.

Only the groovy version has support for numbering bookmarks to retain their order in Internet Explorer.
Apart from that they are functionally equivalent

Example input yaml:

    ---
    - name: test bookmarks
      bookmarks:
      - name: Google
        url: https://www.google.com
      - name: Wikipedia
        bookmarks:
        - name: Wikipedia English Home
          url: https://en.wikipedia.org/
          icon: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAA7klEQVQ4ja2TUZFDIQxFQUUkREQURAoSMIADLGAgNjCAgcjg7scW5tFuu9PtZoaPADkhNySEm8058c4KV3s3+ID8NXhDPgb03iEiCCGg1oo5J1R1++4OEYGIwN3RWgMRQVUxxvh+gbsjxggzw/KZ+QC6+84qIo8lqOpxsDKZGUopx/4CH4DeO0II6L3vQ2YGMx81q+pzEVX1uJBSAhHhmiTn/BxgZogxYoyxgUSE1toGXrX4sY3MjJwzzAy1VpRSwMxwd6SUfv8HrTXEGHcpq0Micujz8iMR0aF0SulBvJeApcF13df+L/Pw0USuuC9zNVPvSNmuzAAAAABJRU5ErkJggg=='
        - name: ---
        - name: Wiki Buthan
          url: https://en.wikipedia.org/wiki/Bhutan
          tags:
          - country
          - asia
        - name: Nepal
          url: https://en.wikipedia.org/wiki/Nepal
          description: A country in the Himalayas
      - name: Youtube
        url: https://www.youtube.com
