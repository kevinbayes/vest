package me.bayes.vertx.vest;

import java.util.HashSet;
import java.util.Set;

import me.bayes.vertx.vest.sample.BaseSampleProvider;
import me.bayes.vertx.vest.sample.LateSampleProvider;
import me.bayes.vertx.vest.sample.OtherSampleProvider;
import me.bayes.vertx.vest.sample.SampleProvider;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TestVertxApplicationProviders {

	@Test
	public void testGivenPackageToScanOneClassFound() {
		// Given
		VestApplication application = new VestApplication() {
		};
		Set<String> packages = new HashSet<String>(1);
		packages.add("me.bayes.vertx.vest.sample");

		// When
		application.addPackagesToScan(packages);

		// Then
		Assertions.assertThat(application.getProviders(SampleProvider.class))
				.hasSize(3)
				.containsSequence(BaseSampleProvider.class, OtherSampleProvider.class, LateSampleProvider.class);
	}

}
