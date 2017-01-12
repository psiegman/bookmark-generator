@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.*
import org.yaml.snakeyaml.constructor.*
import groovy.transform.*

SEPARATOR_NAME = '---'

// parse command-line arguments
def cli = new CliBuilder(usage:'bookmark_generator.groovy [options] inputfile')
cli.with {
    h(longOpt: 'help', 'Help - Usage Information')
    t(longOpt: 'tagall', args:1, 'Tag to add to every bookmark')
    n(longOpt: 'numberall', 'Add numbering to all bookmarks to retain order')
}

if('-h' in args || '--help' in args) {
    cli.usage()
    return
}

def options = cli.parse(args)
if(! options) {
    System.exit(1)
}

if (! options.arguments()) {
    cli.usage()
    return
}

number_all = options.n
tag_all = options.t
def filename = options.arguments()[0]

// load bookmark daa
def rootLevelBookmarks = new Yaml().load(new FileReader(filename))

// Writer out = new FileWriter("intergamma-dev-shared-bookmarks.html")
def out = new OutputStreamWriter(System.out)

// write bookmark file header
out.write('''<!DOCTYPE NETSCAPE-Bookmark-file-1>
<!-- This is an automatically generated file.
     It will be read and overwritten.
     DO NOT EDIT! -->
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<TITLE>Bookmarks</TITLE>
<H1>Bookmarks</H1>

''')

generateBookmarks(rootLevelBookmarks, 0, out)

out.flush()


def isSeparator(bookmark) {
	SEPARATOR_NAME == bookmark.name && (! bookmark.bookmarks)
}

// recursively writes bookmark data to out
def generateBookmarks(nodes, level, out) {
	def indent = ''
	level.times { indent += "    "}

	def nrDigits = -1
	if (number_all) {
		nrDigits = Math.ceil(Math.log10(nodes.findAll{! isSeparator(it)}.size()))
		if (nrDigits == 1) {
			nrDigits = 2
		}
	}

	def orderCounter = 1
	out.write("${indent}<DL><p>\n")
	nodes.each { node ->
		
		// counter
		def counter = ''
		if (nrDigits > 0) {
			counter = ('' + (orderCounter++)).padLeft(nrDigits, '0') + " "
		}

		// a folder
		if (node.folder) {
			out.write("${indent}<DT><H3>${counter}${node.folder}</H3>\n")
			if (node.bookmarks) {
				generateBookmarks(node.bookmarks, level + 1, out)
			}

		// a separator
        } else if (isSeparator(node)) {
			out.write("${indent}<HR>\n")
			orderCounter--

		// a bookmark
		} else if (node.url) {
			out.write("${indent}<DT><A")

			// write url
			out.write(" HREF=\"${node.url}\"")

			// write icon, data uri format (https://en.wikipedia.org/wiki/Data_URI_scheme)
			if (node.icon) {
				out.write(" ICON=\"${node.icon}\"")
			}

			// write tags
			if (tag_all || node.tags) {
				def tags = []

				if (tag_all) {
					tags.add(tag_all)
				}

				if (node.tags) {
					tags.addAll(node.tags)
				}

				out.write(" TAGS=\"${tags.join(",")}\"")
			}

			out.write(">")


			// write bookmark name
			out.write("${counter}${node.name}")

			out.write("</A>\n")

			// write description
			if (node.description) {
				out.write("${indent}<DD>${node.description}\n")
			}
		} else {
			System.err.println("Don't know how to handle ${node}: it is not a bookmark, folder or separator")
		}
	}
	out.write("${indent}</DL><p>\n")
}
