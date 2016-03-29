/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smartsoft.rxcursor;

import android.database.Cursor;
import rx.Observable;

public class CursorObservable {
    /**
     * Create Observable that emits the specified {@link android.database.Cursor} for each available position
     * of the cursor moving to the next position before each call and closing the cursor whether the
     * Observable completes or an error occurs.
     */
    public static Observable<Cursor> fromCursor(final Cursor cursor) {
        return Observable.create(new OnSubscribeCursor(cursor));
    }
}
