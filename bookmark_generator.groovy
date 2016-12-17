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

if (! options.arguments) {
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
def generateBookmarks(bookmarks, level, out) {
	def indent = ''
	level.times { indent += "    "}

	def nrDigits = -1
	if (number_all) {
		nrDigits = Math.ceil(Math.log10(bookmarks.findAll{! isSeparator(it)}.size()))
		if (nrDigits == 1) {
			nrDigits = 2
		}
	}

	def orderCounter = 1
	out.write("${indent}<DL><p>\n")
	bookmarks.each { bookmark ->

		// counter
		def counter = ''
		if (nrDigits > 0) {
			counter = ('' + (orderCounter++)).padLeft(nrDigits, '0') + " "
		}

		// a bookmark
		if (bookmark.url) {
			out.write("${indent}<DT><A")

			// write url
			out.write(" HREF=\"${bookmark.url}\"")

			// write icon, data uri format (https://en.wikipedia.org/wiki/Data_URI_scheme)
			if (bookmark.icon) {
				out.write(" ICON=\"${bookmark.icon}\"")
			}

			// write tags
			if (tag_all || bookmark.tags) {
				def tags = []

				if (tag_all) {
					tags.add(tag_all)
				}

				if (bookmark.tags) {
					tags.addAll(bookmark.tags)
				}

				out.write(" TAGS=\"${tags.join(",")}\"")
			}

			out.write(">")


			// write bookmark name
			out.write("${counter}${bookmark.name}")

			out.write("</A>\n")

			// write description
			if (bookmark.description) {
				out.write("${indent}<DD>${bookmark.description}\n")
			}

		// a separator
        } else if (isSeparator(bookmark)) {
			out.write("${indent}<HR>\n")
			orderCounter--

		// a folder
		} else {
			out.write("${indent}<DT><H3>${counter}${bookmark.name}</H3>\n")
			if (bookmark.bookmarks) {
				generateBookmarks(bookmark.bookmarks, level + 1, out)
			}
		}
	}
	out.write("${indent}</DL><p>\n")
}
