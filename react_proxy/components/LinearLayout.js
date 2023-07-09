import React, {Component} from 'react';
import {StyleSheet, Pressable, View, Text, TextInput} from 'react-native';
import Button from './Button';
import EditText from './EditText';
import TextView from './TextView';
import ImageView from './ImageView';
import OtherView from './OtherView';


class LinearLayout extends Component{
    constructor(props) {
        super(props);
        console.log(Date.now()," : ","new LinearLayout instance created");
        this.state = {
            thisData: this.props.setLinearLayout,
            UIList: this.props.UIList
            
        };
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
                //console.log(Date.now()," : ","print child");
                if(item.WidgetType.includes("EditText")){
                    console.log(Date.now()," : ","LinearLayout : passing to EditText");
                    return (
                        <EditText 
                            key={item.ID}
                            setEditText={item}
                            position={"automatic"}
                            TextChangeListener={this.props.TextChangeListener}/>
                    );
                }
                if(item.WidgetType.includes("TextView")){
                    console.log(Date.now()," : ","LinearLayout : passing to TextView");
                    return (
                        <TextView 
                            key={item.ID}
                            setTextView={item}
                            position={"automatic"}
                            TextChangeListener={this.props.TextChangeListener}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                        
                    );
                }
                if(item.WidgetType.includes("Button")){
                    console.log(Date.now()," : ","LinearLayout : passing to Button");
                    return(
                        <Button 
                            key={item.ID}
                            setButton={item}
                            position={"automatic"}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
                if(item.WidgetType.includes("ImageView")) {
                    console.log(Date.now()," : ","LinearLayout : ImageView");
                    return (
                        <ImageView
                            key={item.ID}
                            setImageView={item}
                            position={"automatic"}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
                if(item.WidgetType.includes("OtherView")) {
                    console.log(Date.now()," : ","LinearLayout : OtherView");
                    return (
                        <OtherView
                            key={item.ID}
                            setOtherView={item}
                            position={"automatic"}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
            }
            else{
                // console.log(Date.now()," : ",'LinearLayout : not child');
            }
            
        });

        if(this.state.thisData.Orientation == 0)
        {
            console.log(Date.now()," : ","LinearLayout : column");
            return(
                <View 
                    key={this.state.thisData.ID}
                    style={{flexDirection: 'row', height: this.state.thisData.Height+ 10, width: this.state.thisData.Width + 10, backgroundColor: 'blanchedalmond'}}>
                    {UIs}
                </View>
            )
        }
        else
        {
            console.log(Date.now()," : ","LinearLayout : row");
            return(
                <View 
                    key={this.state.thisData.ID}
                    style={{flexDirection: 'column', height: this.state.thisData.Height+ 10, width: this.state.thisData.Width + 10, backgroundColor: 'blanchedalmond'}}>
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
