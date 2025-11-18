package com.revamp.booking.blackbox;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.revamp.booking.repository.BookingRepository;
import com.revamp.booking.repository.ModificationItemRepository;
import com.revamp.booking.service.BookingService;
import com.revamp.booking.service.StripeService;

@TestConfiguration
public class MockConfiguration {

    @Bean
    @Primary
    public BookingRepository mockBookingRepository() {
        return Mockito.mock(BookingRepository.class);
    }

    @Bean
    @Primary
    public ModificationItemRepository mockModificationItemRepository() {
        return Mockito.mock(ModificationItemRepository.class);
    }

    @Bean
    @Primary
    public BookingService mockBookingService() {
        return Mockito.mock(BookingService.class);
    }

    @Bean
    @Primary
    public StripeService mockStripeService() {
        return Mockito.mock(StripeService.class);
    }
}
