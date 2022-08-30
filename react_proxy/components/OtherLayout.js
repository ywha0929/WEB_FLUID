import React, {Component} from 'react';
import {StyleSheet, Pressable, View, Text, TextInput} from 'react-native';
import Button from './Button';
import EditText from './EditText';
import TextView from './TextView';
import ImageView from './ImageView';

class OtherLayout extends Component{
    constructor(props) {
        super(props);
        this.state = {
            thisData: this.props.setOtherLayout,
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
                            key={item.ID}
                            setEditText={item}
                            position={"cordinate"}
                            TextChangeListener={this.props.TextChangeListener}
                            />
                    );
                }
                if(item.WidgetType.includes("TextView")){
                    console.log("TextView");
                    return (
                        <TextView 
                            key={item.ID}
                            setTextView={item}
                            position={"cordinate"}
                            TextChangeListener={this.props.TextChangeListener}/>
                        
                    );
                }
                if(item.WidgetType.includes("Button")){
                    console.log("Button");
                    return(
                        <Button 
                            key={item.ID}
                            setButton={item} 
                            position={"cordinate"}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
                if(item.WidgetType.includes("ImageView")) {
                    console.log("ImageView");
                    return (
                        <ImageView
                            key={item.ID}
                            setImageView={item}
                            position={"cordinate"}/>
                    )
                }
            }
            else{
                console.log('not child');
            }
            
        });


        console.log("OtherLayout : ", this.state.thisData);
        return(
            <View 
                key={this.state.thisData.ID}
                style={{height: this.state.thisData.Height, width: this.state.thisData.Width, borderBottomWidth: StyleSheet.hairlineWidth}}>
                {UIs}
            </View>
        )
        
        
        
    }
}

const styles = StyleSheet.create({
    LinearLayout:{

    }
})

export default OtherLayout