# URL-Broken-Link-and-Accessibility-Checker
Broken Link Checker is an application that checks a website and report the status of all the links (broken or active). I have also added additional features which will help the blind users, by reading a web page by screen readers, and which will help the search engine to find out some details about a web page.

Program Implementation

• Broken link checker uses breadth-first-search (BFS) to search for links in a web page.
That means the program first report all the links in a web page. Then it searches the reported links one by one recursively up to the user input depth.
• Program will report all the child links under a parent URL (as in a tree) when it found a link.

• Program also report the status of the link, whether it is active or broken, when it found a link.

• Program maintains three collections to process webpage: waiting-URL-queue (to be processed), processed-URL (already processed), and error-URL. addURL() method adds the URL in the waiting-URL-queue queue.

• Program has a background thread, which runs the broken_link class. This background thread ensures that user interface is enabled while program is adding/ removing/ reporting URLs of the website.

• Program has a parser class that parses the links for html tags and attributes.

• FoundURL() method checks the link status by opening a URL connection, and show the output in the GUI.
