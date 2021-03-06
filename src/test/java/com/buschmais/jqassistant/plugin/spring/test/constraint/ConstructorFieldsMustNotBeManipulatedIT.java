package com.buschmais.jqassistant.plugin.spring.test.constraint;

import static com.buschmais.jqassistant.core.analysis.api.Result.Status.FAILURE;
import static com.buschmais.jqassistant.core.analysis.test.matcher.ConstraintMatcher.constraint;
import static com.buschmais.jqassistant.core.analysis.test.matcher.ResultMatcher.result;
import static com.buschmais.jqassistant.plugin.java.test.matcher.MethodDescriptorMatcher.methodDescriptor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.buschmais.jqassistant.core.analysis.api.Result;
import com.buschmais.jqassistant.core.analysis.api.rule.Constraint;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;
import com.buschmais.jqassistant.plugin.spring.test.set.constructors.ServiceWritingConstructorField;

public class ConstructorFieldsMustNotBeManipulatedIT extends AbstractJavaPluginIT {

    @Test
    public void constructorFieldsMustNotBeManipulated() throws Exception {
        scanClasses(ServiceWritingConstructorField.class);
        assertThat(validateConstraint("spring-injection:FieldsInitializedByConstructorMustNotBeManipulated").getStatus(), equalTo(FAILURE));
        store.beginTransaction();
        List<Result<Constraint>> constraintViolations = new ArrayList<>(reportWriter.getConstraintResults().values());
        assertThat(constraintViolations.size(), equalTo(1));
        Result<Constraint> result = constraintViolations.get(0);
        assertThat(result, result(constraint("spring-injection:FieldsInitializedByConstructorMustNotBeManipulated")));
        List<Map<String, Object>> rows = result.getRows();
        assertThat(rows.size(), equalTo(1));
        MethodDescriptor method = (MethodDescriptor) rows.get(0).get("Method");
        assertThat(method, methodDescriptor(ServiceWritingConstructorField.class, "setValue", String.class));
        store.commitTransaction();
    }

}
