/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.config.builders;

import org.mule.api.MuleContext;

public class ScriptSource
{
    MuleContext muleContext;

    // TODO MULE-2205 Reimplement this using the registry instead of the QuickConfigurationBuilder.
    public ScriptSource() throws Exception
    {
//        // need this when running with JMX
//        //TODO MULE-1988 
//        muleContext.stop();
//        muleContext.setId("GroovyScriptTestCase");
//        muleContext.start();
//
//        //set global properties
//        muleContext.getRegistry().registerProperty("doCompression", "true");
//        //disable the admin agent
//        //manager.getConfiguration().setServerUrl("");
//
//        //Set a dummy TX manager
//        muleContext.setTransactionManager(new TestTransactionManagerFactory().create());
//
//        //register agents
//        RmiRegistryAgent rmiAgent = new RmiRegistryAgent();
//        rmiAgent.setName("rmiAgent");
//        muleContext.getRegistry().registerAgent(rmiAgent);
//
//        JmxAgent agent = new JmxAgent();
//        agent.setName("jmxAgent");
//        agent.setConnectorServerUrl("service:jmx:rmi:///jndi/rmi://localhost:1099/server");
//        Map p = new HashMap();
//        p.put("jmx.remote.jndi.rebind", "true");
//        agent.setConnectorServerProperties(p);
//        muleContext.getRegistry().registerAgent(agent);
//
//        //register connector
//        TestConnector c = new TestConnector();
//        c.setName("dummyConnector");
//        c.setExceptionListener(new TestExceptionStrategy());
//        muleContext.getRegistry().registerConnector(c);
//
//        //Register transformers
//        TestCompressionTransformer t = new TestCompressionTransformer();
//        t.setReturnClass(String.class);
//        t.setBeanProperty2(12);
//        t.setContainerProperty("");
//        t.setBeanProperty1("this was set from the manager properties!");
//        muleContext.getRegistry().registerTransformer(t);
//
//        //Register endpoints
//        JXPathFilter filter = new JXPathFilter("name");
//        filter.setValue("bar");
//        Map ns = new HashMap();
//        ns.put("foo", "http://foo.com");
//        filter.setNamespaces(ns);
//        builder.registerEndpoint("test://fruitBowlPublishQ", "fruitBowlEndpoint", false, null, filter);
//        builder.registerEndpoint("test://AppleQueue", "appleInEndpoint", true);
//        builder.registerEndpoint("test://AppleResponseQueue", "appleResponseEndpoint", false);
//        builder.registerEndpoint("test://apple.queue", "AppleQueue", false);
//        builder.registerEndpoint("test://banana.queue", "Banana_Queue", false);
//        builder.registerEndpoint("test://test.queue", "waterMelonEndpoint", false);
//        Endpoint ep = new MuleEndpoint("test://test.queue2", false);
//        ep.setName("testEPWithCS");
//        SimpleRetryConnectionStrategy cs = new SimpleRetryConnectionStrategy();
//        cs.setRetryCount(4);
//        cs.setRetryFrequency(3000);
//        ep.setConnectionStrategy(cs);
//        builder.getMuleContext().getRegistry().registerEndpoint(ep);
//
//        Map props = new HashMap();
//        props.put("testGlobal", "value1");
//        builder.registerEndpoint("test://orangeQ", "orangeEndpoint", false, props);
//
//        //register model
//        Model model = new SedaModel();
//        model.setName("main");
//        TestExceptionStrategy es = new TestExceptionStrategy();
//        es.addEndpoint(new MuleEndpoint("test://service.exceptions", false));
//        model.setExceptionListener(es);
//        model.setLifecycleAdapterFactory(new TestDefaultLifecycleAdapterFactory());
//        model.setEntryPointResolver(new TestEntryPointResolver());
//        muleContext.getRegistry().registerModel(model);
//
//        //register components
//        Endpoint ep1 = muleContext.getRegistry().lookupEndpoint("appleInEndpoint");
//        ep1.setTransformer(muleContext.getRegistry().lookupTransformer("TestCompressionTransformer"));
//        UMODescriptor d = builder.createDescriptor(Orange.class.getName(), "orangeComponent", null, ep1, props);
//
//        DefaultServiceExceptionStrategy dces = new DefaultServiceExceptionStrategy();
//        dces.addEndpoint(new MuleEndpoint("test://orange.exceptions", false));
//        d.setExceptionListener(dces);
//        //Create the inbound router
//        InboundRouterCollection inRouter = new DefaultInboundRouterCollection();
//        inRouter.setCatchAllStrategy(new ForwardingCatchAllStrategy());
//        inRouter.getCatchAllStrategy().setEndpoint(new MuleEndpoint("test2://catch.all", false));
//        Endpoint ep2 = builder.createEndpoint("test://orange/", "Orange", true, "TestCompressionTransformer");
//        ep2.setResponseTransformer(muleContext.getRegistry().lookupTransformer("TestCompressionTransformer"));
//        inRouter.addEndpoint(ep2);
//        Endpoint ep3 = muleContext.getRegistry().lookupEndpoint("orangeEndpoint");
//        ep3.setFilter(new PayloadTypeFilter(String.class));
//        ep3.setTransformer(muleContext.getRegistry().lookupTransformer("TestCompressionTransformer"));
//        Map props2 = new HashMap();
//        props2.put("testLocal", "value1");
//        ep3.setProperties(props2);
//        inRouter.addEndpoint(ep3);
//        d.setInboundRouter(inRouter);
//
//        //Nested Router
//        NestedRouterCollection nestedRouter = new DefaultNestedRouterCollection();
//        DefaultNestedRouter nr1 = new DefaultNestedRouter();
//        nr1.setEndpoint(new MuleEndpoint("test://do.wash", false));
//        nr1.setInterface(FruitCleaner.class);
//        nr1.setMethod("wash");
//        nestedRouter.addRouter(nr1);
//        DefaultNestedRouter nr2 = new DefaultNestedRouter();
//        nr2.setEndpoint(new MuleEndpoint("test://do.polish", false));
//        nr2.setInterface(FruitCleaner.class);
//        nr2.setMethod("polish");
//        nestedRouter.addRouter(nr2);
//        d.setNestedRouter(nestedRouter);
//
////Response Router
//        ResponseRouterCollection responseRouter = new DefaultResponseRouterCollection();
//        responseRouter.addEndpoint(new MuleEndpoint("test://response1", true));
//        responseRouter.addEndpoint(muleContext.getRegistry().lookupEndpoint("appleResponseEndpoint"));
//        responseRouter.addRouter(new TestResponseAggregator());
//        responseRouter.setTimeout(10001);
//        d.setResponseRouter(responseRouter);
//
//        //properties
//        Map cprops = new HashMap();
//        //cprops.put("orange", new Orange());
//        cprops.put("brand", "Juicy Baby!");
//        cprops.put("segments", "9");
//        cprops.put("radius", "4.21");
//        Map nested = new HashMap();
//        nested.put("prop1", "prop1");
//        nested.put("prop2", "prop2");
//        cprops.put("mapProperties", nested);
//        List nested2 = new ArrayList();
//        nested2.add("prop1");
//        nested2.add("prop2");
//        nested2.add("prop3");
//        cprops.put("listProperties", nested2);
//        List nested3 = new ArrayList();
//        nested3.add("prop4");
//        nested3.add("prop5");
//        nested3.add("prop6");
//        cprops.put("arrayProperties", nested3);
//        d.setProperties(cprops);
//
//        d.setModelName("main");
//
//        //register components
//        muleContext.getRegistry().registerService(d);
    }
}