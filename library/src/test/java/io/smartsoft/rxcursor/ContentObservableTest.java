package io.smartsoft.rxcursor;

import android.database.Cursor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ContentObservableTest {

    @Test
    public void givenCursorWhenFromCursorInvokedThenObservableCallsOnNextWhileHasNext() {
        final Subscriber<Cursor> subscriber = spy(new TestSubscriber<Cursor>());
        final Cursor cursor = mock(Cursor.class);

        when(cursor.isAfterLast()).thenReturn(false, false, true);
        when(cursor.moveToNext()).thenReturn(true, true, false);
        when(cursor.getCount()).thenReturn(2);

        Observable<Cursor> observable = CursorObservable.fromCursor(cursor);
        observable.subscribe(subscriber);

        verify(subscriber, times(2)).onNext(cursor);
        verify(subscriber, never()).onError(Matchers.any(Throwable.class));
        verify(subscriber).onCompleted();
    }

    @Test
    public void givenEmptyCursorWhenFromCursorInvokedThenObservableCompletesWithoutCallingOnNext() {
        final Subscriber<Cursor> subscriber = spy(new TestSubscriber<Cursor>());
        final Cursor cursor = mock(Cursor.class);

        Observable<Cursor> observable = CursorObservable.fromCursor(cursor);
        observable.subscribe(subscriber);

        verify(subscriber, never()).onNext(cursor);
        verify(subscriber, never()).onError(Matchers.any(Throwable.class));
        verify(subscriber).onCompleted();
    }

    @Test
    public void givenCursorWhenFromCursorCalledThenEmitsAndClosesCursorAfterCompletion() {
        final Subscriber<Cursor> subscriber = spy(new TestSubscriber<Cursor>());
        final Cursor cursor = mock(Cursor.class);

        when(cursor.isAfterLast()).thenReturn(false, true);
        when(cursor.moveToNext()).thenReturn(true, false);
        when(cursor.getCount()).thenReturn(1);

        Observable<Cursor> observable = CursorObservable.fromCursor(cursor);
        observable.subscribe(subscriber);

        verify(subscriber, never()).onError(Matchers.any(Throwable.class));
        verify(subscriber).onNext(cursor);
        verify(cursor).close();
        verify(subscriber).onCompleted();
    }

    @Test
    public void givenCursorWhenFromCursorCalledThenEmitsAndClosesCursorAfterSubscriberError() {
        final Subscriber<Cursor> subscriber = spy(new TestSubscriber<Cursor>());
        final Cursor cursor = mock(Cursor.class);
        final RuntimeException throwable = mock(RuntimeException.class);

        doThrow(throwable).when(subscriber).onNext(cursor);

        when(cursor.isAfterLast()).thenReturn(false, true);
        when(cursor.moveToNext()).thenReturn(true, false);
        when(cursor.getCount()).thenReturn(1);

        Observable<Cursor> observable = CursorObservable.fromCursor(cursor);
        observable.subscribe(subscriber);

        verify(subscriber, never()).onCompleted();
        verify(subscriber).onNext(cursor);
        verify(subscriber).onError(throwable);
        verify(cursor).close();
    }

    @Test
    public void givenCursorWhenFromCursorCalledThenEmitsAndClosesCursorAfterObservableError() {
        final Subscriber<Cursor> subscriber = spy(new TestSubscriber<Cursor>());
        final Cursor cursor = mock(Cursor.class);
        final RuntimeException throwable = mock(RuntimeException.class);

        when(cursor.isAfterLast()).thenReturn(false, false, true);
        when(cursor.moveToNext()).thenReturn(true).thenThrow(throwable);
        when(cursor.getCount()).thenReturn(2);

        Observable<Cursor> observable = CursorObservable.fromCursor(cursor);
        observable.subscribe(subscriber);

        verify(subscriber, never()).onCompleted();
        verify(subscriber).onNext(cursor);
        verify(subscriber).onError(throwable);
        verify(cursor).close();
    }

}