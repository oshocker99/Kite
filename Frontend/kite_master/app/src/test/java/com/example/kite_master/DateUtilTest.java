package com.example.kite_master;

import com.team100.kite_master.forum.forum_data_classes.DateUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilTest {

    DateUtil d;


    @Before
    public void setUp() {
        d = mock(DateUtil.class);
    }

    @Test
    public void testTimeAgo1() {
        when(d.getCurMillis()).thenReturn(1554676736748L);
        when(d.getTimeAgo(1551997422)).thenCallRealMethod();
        assertEquals(d.getTimeAgo(1551997422), "31 days ago");
    }

    @Test
    public void testTimeAgo2() {
        when(d.getCurMillis()).thenReturn(1554676736748L);
        when(d.getTimeAgo(1554585008)).thenCallRealMethod();
        assertEquals(d.getTimeAgo(1554585008), "yesterday");
    }

    @Test
    public void testTimeAgo3() {
        when(d.getCurMillis()).thenReturn(1554676736748L);
        when(d.getTimeAgo(1554239393)).thenCallRealMethod();
        assertEquals(d.getTimeAgo(1554239393), "5 days ago");
    }


}
