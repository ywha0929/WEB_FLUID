import React, {Component} from 'react';
import {StyleSheet, Pressable, View, Text, TextInput} from 'react-native';
import Button from './Button';
import EditText from './EditText';
import TextView from './TextView';

class LinearLayout extends Component{
    constructor(props) {
        super(props);
        console.log(this.props.setLinearLayout);
        this.state = {
            thisData: this.props.setLinearLayout,
            UIList: this.props.UIList
            
        };
    }
    TextChangeListener=(e)=>{
        this.props.TextChangeListener(e);
        console.log("this is dummy TextChangeListener of LinearLayout");
    }
    onPressInListener=(e)=>{
        this.props.onPressInListener(e);
        console.log("this is dummy onPressInListener of LinearLayout");
    }
    onPressOutListener=(e)=>{
        this.props.onPressOutListener(e);
        console.log("this is dummy onPressOutListener of LinearLayout");
    }
    render() {
        let UIs = this.state.UIList.map((item,index)=>{
            if(item.Parent_ID == this.state.thisData.ID){
                console.log("print child");
                if(item.WidgetType.includes("EditText")){
                    console.log("EditText");
                    return (
                        <EditText 
                            setEditText={item}
                            TextChangeListener={this.TextChangeListener}/>
                    );
                }
                if(item.WidgetType.includes("TextView")){
                    console.log("TextView");
                    return (
                        <TextView 
                            setTextView={item}
                            TextChangeListener={this.TextChangeListener}/>
                        
                    );
                }
                if(item.WidgetType.includes("Button")){
                    console.log("Button");
                    return(
                        <Button 
                            setButton={item} 
                            onPressInListener={this.onPressInListener}
                            onPressOutListener={this.onPressOutListener}/>
                    )
                }
            }
            else{
                console.log('not child');
            }
            
        });

        if(this.state.thisData.Orientation == 0)
        {
            console.log("column");
            return(
                <View 
                    key={this.state.thisData.ID}
                    style={{flexDirection: 'row'}}>
                    {UIs}
                </View>
            )
        }
        else
        {
            console.log("row");
            return(
                <View 
                    key={this.state.thisData.ID}
                    style={{flexDirection: 'column'}}>
                    {UIs}
                </View>
            )
        }
        
    }
}

const styles = StyleSheet.create({
    LinearLayout:{

    }
})

export default LinearLayout