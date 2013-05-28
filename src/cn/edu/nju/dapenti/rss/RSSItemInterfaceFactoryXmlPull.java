package cn.edu.nju.dapenti.rss;


public class RSSItemInterfaceFactoryXmlPull extends RSSItemInterfaceFactoryMethod{

	@Override
	protected void setRSSHandler() {
		this.handler = new RSSHandlerXmlPullFeed();
	}

}
