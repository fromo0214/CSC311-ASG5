package csudh.csc311.asg5;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;


public class WikiParser 
{	
	private Elements paragraphs;
	
    private Deque<String> parenthesisStack;
	


	public WikiParser(Elements paragraphs) 
    {
		this.paragraphs = paragraphs;
		this.parenthesisStack = new ArrayDeque<String>();
	}

	public Element findFirstLink() 
    {
		for (Element paragraph: paragraphs) 
        { 
			
            Element firstLink = findFirstLinkPara(paragraph);
			if (firstLink != null) 
            {
				return firstLink;
			}
			if (!parenthesisStack.isEmpty()) 
            {

				System.err.println("Parenthesis invalid!"); 
	   	 	}
		}
		return null;
	}

	private Element findFirstLinkPara(Node root) 
    {
		Iterable<Node> nt = new WikiNodeIterable(root);

		for (Node node: nt) 
        {
		
			if (node instanceof TextNode) 
            {
				processTextNode((TextNode) node);
			}

			if (node instanceof Element) 
            {
		        Element firstLink = processElement((Element) node);
		        if (firstLink != null) 
                {
					return firstLink;
				}
			}
		}
		
        
        return null;
	}
	private Element processElement(Element elt) 
    {

		if (validLink(elt)) {
			return elt;
		}
		return null;
	
    }

	private boolean validLink(Element elt) 
    {

		if (!elt.tagName().equals("a")) 
        {
			return false;
		}

		if (isItalic(elt)) 
        {
			return false;
		}

		if (isInParens(elt)) 
        {
			return false;
		}

		if (startsWith(elt, "#")) 
        {
			return false;
		}

		if (startsWith(elt, "/wiki/Help:")) 
        {
			return false;
		}

		return true;
	}

	private boolean startsWith(Element elt, String s) 
    {
		return (elt.attr("href").startsWith(s));
	}

	private boolean isInParens(Element elt) 
    {
		return !parenthesisStack.isEmpty();
	}

	private boolean isItalic(Element start) 
    {
		for (Element elt=start; elt != null; elt = elt.parent()) 
        {
			if (elt.tagName().equals("i") || elt.tagName().equals("em")) 
            {
				return true;
			}
		}
		return false;
	}

	private void processTextNode(TextNode node) 
    {
		StringTokenizer st = new StringTokenizer(node.text(), " ()", true);
		while (st.hasMoreTokens()) 
        {
		     String token = st.nextToken();
		     
             if (token.equals("(")) 
             {
		    	 parenthesisStack.push(token);
		     }
		     
             
             if (token.equals(")")) 
             {
		    	 
                if (parenthesisStack.isEmpty()) 
                 {
		    		 System.err.println("Invalid parenthesis!"); 
		    	 }
		    	
                 parenthesisStack.pop();
		     }
		}
	}
}