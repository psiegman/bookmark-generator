#!/usr/bin/env python
"""
Converts an yaml file with bookmark data to a a bookmark html file that can be imported in Firefox, Chrome and Internet explorer.

Supports name, url, description, tags and icons.

Example input yaml::

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
"""

import yaml
import sys
import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-t', '--tag', help="add a tag to all bookmarks")
parser.add_argument("inputfile", help="the bookmark input file")
args = parser.parse_args()

bookmark_data = yaml.load(open(args.inputfile))

out = sys.stdout

SEPARATOR_NAME = u'---'

tag_all = args.tag

# write bookmark file header
out.write('''<!DOCTYPE NETSCAPE-Bookmark-file-1>
<!-- This is an automatically generated file.
     It will be read and overwritten.
     DO NOT EDIT! -->
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<TITLE>Bookmarks</TITLE>
<H1>Bookmarks</H1>

''')


def writeBookmarks(bookmarks, level, out):
	indent = '    '*level

	out.write(indent + '<DL><p>\n')
	for bookmark in bookmarks:

		# a bookmark
		if 'url' in bookmark:
			out.write(indent + '<DT><A')

			# write href
			out.write(' HREF="' + bookmark['url'] + '"')

			# write icon, data uri format (https://en.wikipedia.org/wiki/Data_URI_scheme)
			if 'icon' in bookmark:
				out.write(' ICON="' + bookmark['icon'] + '"')

			# write tags
			if tag_all or 'tags' in bookmark:
				tags = []

				if tag_all:
					tags.append(tag_all)

				if 'tags' in bookmark:
					tags.extend(bookmark['tags'])

				out.write(' TAGS="' + ",".join(tags) + '"')

			out.write('>')
			out.write(bookmark['name'])
			out.write('</A>')
			out.write('\n')

			# write description
			if 'description' in bookmark:
				out.write(indent + '<DD>' + bookmark['description'] + '\n')

		# a separator
		elif SEPARATOR_NAME == bookmark['name'] and not 'bookmarks' in bookmarks:
			out.write(indent + '<HR>\n')
		else:
			out.write(indent + '<DT><H3>' + bookmark['name'] + '</H3>\n')

		if 'bookmarks' in bookmark:
			writeBookmarks(bookmark['bookmarks'], level + 1, out)
	out.write(indent + '</DL><p>\n')

writeBookmarks(bookmark_data, 0, out)

out.flush()
