/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.impl.index.annotations;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import org.objectweb.asm.ClassReader;

public class AsmAnnotationsScannerTest extends TestCase
{
    protected AsmAnnotationsScanner scanner;

    protected void setUp() throws IOException
    {
        //ClassReader r = new ClassReader(new FileInputStream("c:\\dl\\SampleClassWithAnnotations.class"));
        ClassReader r = new ClassReader(SampleClassWithAnnotations.class.getName());
        scanner = new AsmAnnotationsScanner();

        r.accept(scanner, 0);
    }

    public void testParamAnnotations() throws Exception
    {
        final List<AnnotationInfo> paramAnnotations = scanner.getParamAnnotations();

        System.out.println("Parameter annotations: " + paramAnnotations);

        assertNotNull(paramAnnotations);
        assertEquals(2, paramAnnotations.size());

        // @Marker("ParamLevel")
        AnnotationInfo ann = paramAnnotations.get(0);
        assertEquals(Marker.class.getName(), ann.className);
        List<AnnotationInfo.NameValue> annValues = ann.params;
        assertNotNull(annValues);
        assertEquals(1, annValues.size());
        assertEquals(new AnnotationInfo.NameValue("value", "ParamLevel"), annValues.get(0));

        // @MultiMarker(value = "ParamLevel", param1 = "12", param2 = "abc")
        ann = paramAnnotations.get(1);
        assertEquals(MultiMarker.class.getName(), ann.className);
        annValues = ann.params;
        assertNotNull(annValues);
        assertEquals(3, annValues.size());
        assertEquals(new AnnotationInfo.NameValue("value", "ParamLevel"), annValues.get(0));
        assertEquals(new AnnotationInfo.NameValue("param1", "12"), annValues.get(1));
        assertEquals(new AnnotationInfo.NameValue("param2", "abc"), annValues.get(2));
    }

    public void testFieldAnnotations() throws Exception
    {
        final List<AnnotationInfo> fieldAnnotations = scanner.getFieldAnnotations();

        System.out.println("Field annotations: " + fieldAnnotations);

        assertNotNull(fieldAnnotations);
        assertEquals(1, fieldAnnotations.size());

        // @Marker("FieldLevel")
        AnnotationInfo ann = fieldAnnotations.get(0);
        assertEquals(Marker.class.getName(), ann.className);
        final List<AnnotationInfo.NameValue> annValues = ann.params;
        assertNotNull(annValues);
        assertEquals(1, annValues.size());
        assertEquals(new AnnotationInfo.NameValue("value", "FieldLevel"), annValues.get(0));
    }

    public void testClassAnnotations() throws Exception
    {
        final List<AnnotationInfo> classAnnotations = scanner.getClassAnnotations();

        System.out.println("Class annotations: " + classAnnotations);

        assertNotNull(classAnnotations);
        assertEquals(1, classAnnotations.size());

        // @Marker("ClassLevel")
        AnnotationInfo ann = classAnnotations.get(0);
        assertEquals(Marker.class.getName(), ann.className);
        final List<AnnotationInfo.NameValue> annValues = ann.params;
        assertNotNull(annValues);
        assertEquals(1, annValues.size());
        assertEquals(new AnnotationInfo.NameValue("value", "ClassLevel"), annValues.get(0));
    }

    public void testMethodAnnotations() throws Exception
    {
        final List<AnnotationInfo> methodAnnotations = scanner.getMethodAnnotations();

        System.out.println("Method annotations: " + methodAnnotations);

        assertNotNull(methodAnnotations);
        assertEquals(2, methodAnnotations.size());

        // @Marker("MethodLevel / Main")
        AnnotationInfo ann = methodAnnotations.get(0);
        assertEquals(Marker.class.getName(), ann.className);
        List<AnnotationInfo.NameValue> annValues = ann.params;
        assertNotNull(annValues);
        assertEquals(1, annValues.size());
        assertEquals(new AnnotationInfo.NameValue("value", "MethodLevel / Main"), annValues.get(0));

        // @Marker("MethodLevel / toString")
        ann = methodAnnotations.get(1);
        assertEquals(Marker.class.getName(), ann.className);
        annValues = ann.params;
        assertNotNull(annValues);
        assertEquals(1, annValues.size());
        assertEquals(new AnnotationInfo.NameValue("value", "MethodLevel / toString"), annValues.get(0));


    }

}
