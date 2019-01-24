package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.camp.bit.todolist.beans.Note;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;
import com.camp.bit.todolist.debug.DebugActivity;
import com.camp.bit.todolist.ui.NoteListAdapter;

import org.w3c.dom.Node;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private SQLiteDatabase db;
    private TodoDbHelper mtodoDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mtodoDbHelper = new TodoDbHelper(this);
        db = mtodoDbHelper.getWritableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        mtodoDbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        Cursor cursor = null;
        List<Note> result = new LinkedList<>();
        try {
            cursor = db.query(TodoContract.TodoEntry.TABLE_NAME,new String[]{TodoContract.TodoEntry._ID
                    ,TodoContract.TodoEntry.COLUMN_NAME_TIME
                    ,TodoContract.TodoEntry.COLUMN_NAME_STATE
                    ,TodoContract.TodoEntry.COLUMN_NAME_MESSAGE}
                    ,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                String msg = cursor.getString(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_NAME_MESSAGE));
                long   dat = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_NAME_TIME));
                int     id = cursor.getInt(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry._ID));
                int  state = cursor.getInt(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_NAME_STATE));

                Note note = new Note(id);

                note.setContent(msg);
                note.setDate(new Date(dat));
                note.setState(State.from(state));

                result.add(note);
            }

        }
        finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return result;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据

        String selection = TodoContract.TodoEntry._ID + " =?";
        String[] selectionArgs = {String.valueOf(note.id)};
        int lines = db.delete(TodoContract.TodoEntry.TABLE_NAME,selection,selectionArgs);
        if(lines != 0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    public void updateNode(Note note) {
        // 更新数据
        ContentValues values= new ContentValues();

        values.put(TodoContract.TodoEntry.COLUMN_NAME_STATE,note.getState().intValue);


        String selection = TodoContract.TodoEntry._ID + " =?";
        String[] selectionArgs = {String.valueOf(note.id)};

        int count = db.update(TodoContract.TodoEntry.TABLE_NAME,values,selection,selectionArgs);

        if(count != 0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

}
