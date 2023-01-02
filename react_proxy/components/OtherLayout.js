import React, {Component} from 'react';
import {StyleSheet, Pressable, View, Text, TextInput} from 'react-native';
import Button from './Button';
import EditText from './EditText';
import TextView from './TextView';
import ImageView from './ImageView';
import OtherView from './OtherView';

class OtherLayout extends Component{
    constructor(props) {
        super(props);
        this.state = {
            thisData: this.props.setOtherLayout,
            UIList: this.props.UIList
            
        };
        // console.log(Date.now()," : ","new OtherLayout instance created",props);
    }
    TextChangeListener=(e)=>{
        this.props.TextChangeListener(e);
        console.log(Date.now()," : ","this is dummy TextChangeListener of LinearLayout");
    }

    onPressInListener=(e)=>{
        this.props.onPressInListener(e);
        console.log(Date.now()," : ","this is dummy onPressInListener of LinearLayout");
    }
    onPressOutListener=(e)=>{
        this.props.onPressOutListener(e);
        console.log(Date.now()," : ","this is dummy onPressOutListener of LinearLayout");
    }
    render() {
        let UIs = this.state.UIList.map((item,index)=>{
            if(item.Parent_ID == this.state.thisData.ID){
                if(item.WidgetType.includes("EditText")){
                    console.log(Date.now()," : ","OtherLayout : passing to EditText");
                    return (
                        <EditText 
                            key={item.ID}
                            setEditText={item}
                            position={"coordinate"}
                            TextChangeListener={this.TextChangeListener}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}
                            />
                    );
                }
                if(item.WidgetType.includes("TextView")){
                    console.log(Date.now()," : ","OtherLayout : passing to TextView");
                    return (
                        <TextView 
                            key={item.ID}
                            setTextView={item}
                            position={"coordinate"}
                            TextChangeListener={this.props.TextChangeListener}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                        
                    );
                }
                if(item.WidgetType.includes("Button")){
                    console.log(Date.now()," : ","OtherLayout : passing to Button");
                    return(
                        <Button 
                            key={item.ID}
                            setButton={item} 
                            position={"coordinate"}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
                if(item.WidgetType.includes("ImageView")) {
                    console.log(Date.now()," : ","OtherLayout : passing to ImageView");
                    return (
                        <ImageView
                            key={item.ID}
                            setImageView={item}
                            position={"coordinate"}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
                if(item.WidgetType.includes("OtherView")) {
                    console.log(Date.now()," : ","OtherLayout : passing to OtherView");
                    return (
                        <OtherView 
                            key={item.ID}
                            setOtherView={item}
                            position={"coordinate"}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
            }
            else{
                // console.log(Date.now()," : ",'OtherLayout : not child');
            }
            
        });


        //console.log(Date.now()," : ","OtherLayout : ", this.state.thisData);
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
