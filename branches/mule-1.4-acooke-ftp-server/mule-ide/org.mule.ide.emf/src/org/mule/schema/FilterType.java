/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mule.schema;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Filter Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mule.schema.FilterType#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getFilter <em>Filter</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getLeftFilter <em>Left Filter</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getRightFilter <em>Right Filter</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getClassName <em>Class Name</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getConfigFile <em>Config File</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getExpectedType <em>Expected Type</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getExpression <em>Expression</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getPath <em>Path</em>}</li>
 *   <li>{@link org.mule.schema.FilterType#getPattern <em>Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mule.schema.MulePackage#getFilterType()
 * @model extendedMetaData="name='filterType' kind='mixed'"
 * @generated
 */
public interface FilterType extends EObject {
    /**
     * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mixed</em>' attribute list.
     * @see org.mule.schema.MulePackage#getFilterType_Mixed()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='elementWildcard' name=':mixed'"
     * @generated
     */
    FeatureMap getMixed();

    /**
     * Returns the value of the '<em><b>Properties</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Properties</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Properties</em>' containment reference.
     * @see #setProperties(PropertiesType)
     * @see org.mule.schema.MulePackage#getFilterType_Properties()
     * @model containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='properties' namespace='##targetNamespace'"
     * @generated
     */
    PropertiesType getProperties();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getProperties <em>Properties</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Properties</em>' containment reference.
     * @see #getProperties()
     * @generated
     */
    void setProperties(PropertiesType value);

    /**
     * Returns the value of the '<em><b>Filter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Filter</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Filter</em>' containment reference.
     * @see #setFilter(FilterType)
     * @see org.mule.schema.MulePackage#getFilterType_Filter()
     * @model containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='filter' namespace='##targetNamespace'"
     * @generated
     */
    FilterType getFilter();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getFilter <em>Filter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Filter</em>' containment reference.
     * @see #getFilter()
     * @generated
     */
    void setFilter(FilterType value);

    /**
     * Returns the value of the '<em><b>Left Filter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Left Filter</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Left Filter</em>' containment reference.
     * @see #setLeftFilter(LeftFilterType)
     * @see org.mule.schema.MulePackage#getFilterType_LeftFilter()
     * @model containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='left-filter' namespace='##targetNamespace'"
     * @generated
     */
    LeftFilterType getLeftFilter();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getLeftFilter <em>Left Filter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Left Filter</em>' containment reference.
     * @see #getLeftFilter()
     * @generated
     */
    void setLeftFilter(LeftFilterType value);

    /**
     * Returns the value of the '<em><b>Right Filter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Right Filter</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Right Filter</em>' containment reference.
     * @see #setRightFilter(RightFilterType)
     * @see org.mule.schema.MulePackage#getFilterType_RightFilter()
     * @model containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='right-filter' namespace='##targetNamespace'"
     * @generated
     */
    RightFilterType getRightFilter();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getRightFilter <em>Right Filter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Right Filter</em>' containment reference.
     * @see #getRightFilter()
     * @generated
     */
    void setRightFilter(RightFilterType value);

    /**
     * Returns the value of the '<em><b>Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Class Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Class Name</em>' attribute.
     * @see #setClassName(String)
     * @see org.mule.schema.MulePackage#getFilterType_ClassName()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='className' namespace='##targetNamespace'"
     * @generated
     */
    String getClassName();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getClassName <em>Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Class Name</em>' attribute.
     * @see #getClassName()
     * @generated
     */
    void setClassName(String value);

    /**
     * Returns the value of the '<em><b>Config File</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Config File</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Config File</em>' attribute.
     * @see #setConfigFile(String)
     * @see org.mule.schema.MulePackage#getFilterType_ConfigFile()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='configFile' namespace='##targetNamespace'"
     * @generated
     */
    String getConfigFile();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getConfigFile <em>Config File</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Config File</em>' attribute.
     * @see #getConfigFile()
     * @generated
     */
    void setConfigFile(String value);

    /**
     * Returns the value of the '<em><b>Expected Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Expected Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Expected Type</em>' attribute.
     * @see #setExpectedType(String)
     * @see org.mule.schema.MulePackage#getFilterType_ExpectedType()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='expectedType' namespace='##targetNamespace'"
     * @generated
     */
    String getExpectedType();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getExpectedType <em>Expected Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Expected Type</em>' attribute.
     * @see #getExpectedType()
     * @generated
     */
    void setExpectedType(String value);

    /**
     * Returns the value of the '<em><b>Expression</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Expression</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Expression</em>' attribute.
     * @see #setExpression(String)
     * @see org.mule.schema.MulePackage#getFilterType_Expression()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='expression' namespace='##targetNamespace'"
     * @generated
     */
    String getExpression();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getExpression <em>Expression</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Expression</em>' attribute.
     * @see #getExpression()
     * @generated
     */
    void setExpression(String value);

    /**
     * Returns the value of the '<em><b>Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Path</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Path</em>' attribute.
     * @see #setPath(String)
     * @see org.mule.schema.MulePackage#getFilterType_Path()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='path' namespace='##targetNamespace'"
     * @generated
     */
    String getPath();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getPath <em>Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Path</em>' attribute.
     * @see #getPath()
     * @generated
     */
    void setPath(String value);

    /**
     * Returns the value of the '<em><b>Pattern</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Pattern</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pattern</em>' attribute.
     * @see #setPattern(String)
     * @see org.mule.schema.MulePackage#getFilterType_Pattern()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='pattern' namespace='##targetNamespace'"
     * @generated
     */
    String getPattern();

    /**
     * Sets the value of the '{@link org.mule.schema.FilterType#getPattern <em>Pattern</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pattern</em>' attribute.
     * @see #getPattern()
     * @generated
     */
    void setPattern(String value);

} // FilterType
