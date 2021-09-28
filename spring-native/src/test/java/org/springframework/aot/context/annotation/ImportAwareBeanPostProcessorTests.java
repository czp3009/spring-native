/*
 * Copyright 2019-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aot.context.annotation;

import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ImportAwareBeanPostProcessor}.
 *
 * @author Stephane Nicoll
 */
class ImportAwareBeanPostProcessorTests {

	@Test
	void postProcessOnMatchingCandidate() {
		ImportAwareBeanPostProcessor postProcessor = new ImportAwareBeanPostProcessor(
				Map.of(TestImportAware.class.getName(), ImportAwareBeanPostProcessorTests.class.getName()));
		TestImportAware importAware = new TestImportAware();
		postProcessor.postProcessBeforeInitialization(importAware, "test");
		assertThat(importAware.importMetadata).isNotNull();
		assertThat(importAware.importMetadata.getClassName())
				.isEqualTo(ImportAwareBeanPostProcessorTests.class.getName());
	}

	@Test
	void postProcessOnMatchingCandidateWithNestedClass() {
		ImportAwareBeanPostProcessor postProcessor = new ImportAwareBeanPostProcessor(
				Map.of(TestImportAware.class.getName(), TestImporting.class.getName()));
		TestImportAware importAware = new TestImportAware();
		postProcessor.postProcessBeforeInitialization(importAware, "test");
		assertThat(importAware.importMetadata).isNotNull();
		assertThat(importAware.importMetadata.getClassName())
				.isEqualTo(TestImporting.class.getName());
	}

	@Test
	void postProcessoOnNoCandidateDoesNotInvokeCallback() {
		ImportAwareBeanPostProcessor postProcessor = new ImportAwareBeanPostProcessor(
				Map.of(String.class.getName(), ImportAwareBeanPostProcessorTests.class.getName()));
		TestImportAware importAware = new TestImportAware();
		postProcessor.postProcessBeforeInitialization(importAware, "test");
		assertThat(importAware.importMetadata).isNull();
	}

	@Test
	void postProcessOnMatchingCandidateWithNoMetadata() {
		ImportAwareBeanPostProcessor postProcessor = new ImportAwareBeanPostProcessor(
				Map.of(TestImportAware.class.getName(), "com.example.invalid.DoesNotExist"));
		TestImportAware importAware = new TestImportAware();
		assertThatIllegalStateException().isThrownBy(() -> postProcessor.postProcessBeforeInitialization(importAware, "test"))
				.withMessageContaining("Failed to read metadata for 'com.example.invalid.DoesNotExist'");
	}


	static class TestImportAware implements ImportAware {
		private AnnotationMetadata importMetadata;

		@Override
		public void setImportMetadata(AnnotationMetadata importMetadata) {
			this.importMetadata = importMetadata;
		}
	}

	static class TestImporting {

	}

}
