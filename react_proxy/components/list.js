import React from 'react';
import {StyleSheet, ScrollView, Text} from 'react-native';
import TodoListItem from './listItem'

const UIList = ({EditTextData}) => {
    return (
        <ScrollView contentContainerStyle={styles.listContainer}>
            if(){
            {todos.map(todo => (
                <EditText 
                    key={todo.id}
                    {...todo}/>
            ))}
            }
            
        </ScrollView>
    );
};

const styles = StyleSheet.create({
    listContainer: {
        alignItems: 'center'
    }
});

export default UIList;